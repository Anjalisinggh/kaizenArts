package com.example.kaizenarts.activites;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.example.kaizenarts.R;

import org.json.JSONObject;

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener {

    private double amount = 0.0;
    private double discountAmount = 0.0;
    private double shippingCharge = 60.0; // Example shipping charge

    private Toolbar toolbar;
    private TextView subTotal, discount, shipping, total;
    private Button paymentBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        toolbar = findViewById(R.id.payment_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Retrieve amount (handles both single product and cart purchases)
        amount = getIntent().getDoubleExtra("amount",  0.0);


        Log.d("PaymentActivity", "Received Amount: " + amount);

        if (amount < 0.0) { // ✅ Prevent invalid amount
            Log.e("PaymentActivity", "Invalid amount received. Redirecting...");
            Toast.makeText(this, "Invalid amount received!", Toast.LENGTH_LONG).show();
            finish(); // Close activity if invalid amount
            return;
        }

        subTotal = findViewById(R.id.sub_total);
        discount = findViewById(R.id.textView17);
        shipping = findViewById(R.id.textView18);
        total = findViewById(R.id.total_amt);
        paymentBtn = findViewById(R.id.pay_btn);

        // Apply discount (Example: 10%)
        discountAmount = amount * 0.10;

        // Calculate final total
        double finalTotal = amount - discountAmount + shippingCharge;

        // Update UI
        subTotal.setText(amount + " Rs");
        discount.setText("- " + discountAmount + " Rs");
        shipping.setText("+ " + shippingCharge + " Rs");
        total.setText(finalTotal + " Rs");

        // Set final amount
        amount = finalTotal;

        paymentBtn.setOnClickListener(v -> paymentMethod());
    }

    private void paymentMethod() {
        Checkout checkout = new Checkout();
        final Activity activity = PaymentActivity.this;

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Kaizen Arts");
            options.put("description", "Reference No. #123456");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");

            // Convert to paisa
            double finalAmount = amount * 100;
            options.put("amount", finalAmount);

            JSONObject preFill = new JSONObject();
            preFill.put("email", "anjalisinggh.12@gmail.com");
            preFill.put("contact", "9820392106");

            options.put("prefill", preFill);

            checkout.open(activity, options);

        } catch (Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Failed: " + s, Toast.LENGTH_SHORT).show();
    }
}
