package xiaojianming.testsurfaceview;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ColorPicker colorPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        colorPicker = (ColorPicker) findViewById(R.id.color_picker);
        findViewById(R.id.color_one).setOnClickListener(this);
        findViewById(R.id.color_two).setOnClickListener(this);
        findViewById(R.id.color_three).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.color_one:
                colorPicker.setCurrentColor(Color.GREEN);
                break;
            case R.id.color_two:
                colorPicker.setCurrentColor(Color.YELLOW);
                break;
            case R.id.color_three:
                colorPicker.setCurrentColor(Color.BLUE);
                break;
        }
    }
}
