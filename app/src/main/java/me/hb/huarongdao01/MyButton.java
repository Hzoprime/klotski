package me.hb.huarongdao01;

import android.support.v7.widget.AppCompatImageButton;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;


public class MyButton extends AppCompatImageButton {
    final MyButton view = this;
    static int unit;
    static int verticalCount;
    static int horizontalCount;
    int gameRow;
    int gameColumn;

    ButtonShape shape;

    int gameWidth;
    int gameHeight;

    ArrayList<Coordinate> coordinates;


    static final HashMap<ButtonShape, ArrayList<Integer>> shape2ImageIdArray;

    static {
        verticalCount = 0;
        horizontalCount = 0;
        shape2ImageIdArray = new HashMap<>();
        ArrayList<Integer> horizontalImage = new ArrayList<>();
        horizontalImage.add(R.drawable.h_guanyu);
        horizontalImage.add(R.drawable.h_zhangfei);
        horizontalImage.add(R.drawable.h_zhaoyun);
        horizontalImage.add(R.drawable.h_machao);
        horizontalImage.add(R.drawable.h_huangzhong);

        ArrayList<Integer> verticalImage = new ArrayList<>();
        verticalImage.add(R.drawable.v_guanyu);
        verticalImage.add(R.drawable.v_zhangfei);
        verticalImage.add(R.drawable.v_zhaoyun);
        verticalImage.add(R.drawable.v_machao);
        verticalImage.add(R.drawable.v_huangzhong);

        ArrayList<Integer> blockImage = new ArrayList<>();
        blockImage.add(R.drawable.caocao);

        ArrayList<Integer> pointImage = new ArrayList<>();
        pointImage.add(R.drawable.bing);

        shape2ImageIdArray.put(ButtonShape.horizontal, horizontalImage);
        shape2ImageIdArray.put(ButtonShape.vertical, verticalImage);
        shape2ImageIdArray.put(ButtonShape.block, blockImage);
        shape2ImageIdArray.put(ButtonShape.point, pointImage);

    }

    void init() {
        setId(View.generateViewId());
        setPadding(0, 0, 0, 0);
        setScaleType(ImageView.ScaleType.CENTER_CROP);
        coordinates = new ArrayList<>();
    }

    MyButton(Context context) {
        super(context);
        init();
    }

    MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    MyButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    void setGameShape(final ButtonShape buttonShape) {
        shape = buttonShape;
        gameWidth = shape.width;
        gameHeight = shape.height;
        this.getLayoutParams().height = gameHeight * unit;
        this.getLayoutParams().width = gameWidth * unit;
        if (buttonShape == ButtonShape.point || buttonShape == ButtonShape.block) {
            setImageResource(shape2ImageIdArray.get(buttonShape).get(0));
        } else if (buttonShape == ButtonShape.horizontal) {
            setImageResource(shape2ImageIdArray.get(buttonShape).get(horizontalCount++));
        } else {
            setImageResource(shape2ImageIdArray.get(buttonShape).get(4 - (verticalCount++)));
        }
    }

    void setGameLocation(final int row, final int column) {
        gameRow = row;
        gameColumn = column;
        setX(gameColumn * unit) ;
        setY(gameRow * unit) ;
    }
}