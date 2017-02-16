//package com.carvis;
//
///**
// * Created by Seamus on 16/02/2017.
// */
//
//class MyOverlay extends Overlay{
//
//    public MyOverlay(){
//
//    }
//
//    public void draw(Canvas canvas, MapView mapv, boolean shadow){
//        super.draw(canvas, mapv, shadow);
//
//        Paint   mPaint = new Paint();
//        mPaint.setDither(true);
//        mPaint.setColor(Color.RED);
//        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//        mPaint.setStrokeJoin(Paint.Join.ROUND);
//        mPaint.setStrokeCap(Paint.Cap.ROUND);
//        mPaint.setStrokeWidth(2);
//
//        GeoPoint gP1 = new GeoPoint(19240000,-99120000);
//        GeoPoint gP2 = new GeoPoint(37423157, -122085008);
//
//        Point p1 = new Point();
//        Point p2 = new Point();
//        Path path = new Path();
//
//        Projection projection=mapv.getProjection();
//        projection.toPixels(gP1, p1);
//        projection.toPixels(gP2, p2);
//
//        path.moveTo(p2.x, p2.y);
//        path.lineTo(p1.x,p1.y);
//
//        canvas.drawPath(path, mPaint);
//    }
