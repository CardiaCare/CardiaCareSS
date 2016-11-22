package ru.cardiacare.cardiacare.ecgviewer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import ru.cardiacare.cardiacare.R;

/* Отрисовка ЭКГ */

public class ECGView extends View {

    Paint paint;
    double[] points1;

    private LinkedList<Double> ecg;
    private LinkedList<Double> ecgForHeartRate;
    LinkedList<Double> RRIntervals = new LinkedList<Double>();
    int iterator_ecg = 0;

    double ppmm = 0.0;

    public Timer myTimer;
    public Timer pulseTimer;
    private int pulse = 60;
    int maxX = 0;
    int maxY = 0;
    double squareSize = 0.0;
    int ecgStandartSpeed = 0;

    int[] gridOrigin;

    long ecgLengthScreen = 0;

    public ECGView(Context context, double ppmmActivity) {
        super(context);

        ecg = new LinkedList<Double>();
        ecgForHeartRate = new LinkedList<Double>();
        ecg.add(0.0);

        paint = new Paint();

        ecgStandartSpeed = 40;

        gridOrigin = new int[2];
        gridOrigin[0] = 0;
        gridOrigin[1] = 0;

        ppmm = ppmmActivity;
        squareSize = 6 + 5 * ppmm;
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        maxX = xNew;
        maxY = yNew;

        ecgLengthScreen = Math.round(getWidth() / (2 * squareSize / 50.0));

        points1 = new double[(int) ecgLengthScreen];

        myTimer = new Timer();
        myTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (ecg.size() > ecgLengthScreen) {
                    if (iterator_ecg > ecgLengthScreen)
                        iterator_ecg = 0;
                    else {
                        iterator_ecg += ppmm;
                    }
                    for (int i = 0; i < ppmm; i++) {
                        double item = ecg.removeLast();
                        points1[(iterator_ecg + i) % (int) (ecgLengthScreen)] = item;
                    }
                    postInvalidate();
                }
            }
        }, 2000, ecgStandartSpeed);

        pulseTimer = new Timer();
        pulseTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Data update
                if (RRIntervals.size() > 1) {
                    Double rr = RRIntervals.remove();
                    if ((60.0 / rr) > 40 && (60.0 / rr < 200))
                        pulse = (int) Math.round(60.0 / rr);
//                    Log.i("TAG","pulse " + pulse+ " ecgForHeartRate " + rr);
                    postInvalidate();
                }
            }
        }, 1000, 500);
        super.onSizeChanged(xNew, yNew, xOld, yOld);
    }

    @Override
    public void onDraw(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setSubpixelText(true);
        paint.setAntiAlias(true);

        // Закрашиваем холст белым цветом
        paint.setColor(Color.BLACK);
        canvas.drawPaint(paint);

        // Draw squares
        drawGrid(canvas);

        // Рисуем линии по точкам из массива points1
        drawSignal(canvas, points1);

        paint.setColor(Color.WHITE);
        paint.setTextSize(24);
        canvas.drawText(new Integer(pulse).toString(), 10, 25, paint);
    }

    public void drawSignal(Canvas canvas, double[] s) {

        double gridBaselinePosition = getHeight() / 2;
        paint.setColor(Color.WHITE);
//        context.lineWidth = 2;
//        context.strokeStyle = color;
//        context.beginPath();

        double x0 = gridOrigin[0];
        // TODO убрать 1000
        double y0 = gridBaselinePosition - s[0] * 2 * squareSize / 1000.0;
//        context.moveTo(x, y)
        for (int i = 1; i < s.length; i++) {
            double x = gridOrigin[0] + i * 2 * squareSize / 50.0;
            double y = gridBaselinePosition - s[i] * 2 * squareSize / 1000.0 + 0.5;
            canvas.drawLine((float) x0, (float) y0, (float) x, (float) y, paint);
            x0 = x;
            y0 = y;
        }
//        context.stroke()
    }

    public void drawGrid(Canvas canvas) {
        int leftColumn = (int) Math.round(-gridOrigin[0] / squareSize);
        int rightColumn = (int) Math.round(leftColumn + getWidth() / (squareSize)) + 1;

        int topRow = (int) Math.round(-gridOrigin[1] / squareSize);
        int bottomRow = (int) Math.round(topRow + getHeight() / (squareSize)) + 1;

        double i = (ppmm * 5 - 6) / 5;

        double maxRow = getHeight() / ppmm;
        double maxColumn = getWidth() / ppmm;

        long strokeLine = Math.round(i * 5 + 2);

        // 1 mm lines
        paint.setColor(getResources().getColor(R.color.cardiacare_color_grid));
        paint.setStrokeWidth(1);
        for (int x = 0; x < maxColumn + 1; x++)
            canvas.drawLine((float) (x * getWidth() / maxColumn), 0, (float) (x * getWidth() / maxColumn), getHeight(), paint);
        for (int y = 0; y < maxRow + 1; y++)
            canvas.drawLine(0, (float) (y * getHeight() / maxRow), getWidth(), (float) (y * getHeight() / maxRow), paint);

        // 5 mm lines
        paint.setColor(getResources().getColor(R.color.cardiacare_color_grid));
        paint.setStrokeWidth(3);
        double max5Row = maxRow / 5;
        double max5Column = maxColumn / 5;
        for (int x = 0; x < (int) max5Column + 1; x++)
            canvas.drawLine((float) (x * getWidth() / max5Column), 0, (float) (x * getWidth() / max5Column), getHeight(), paint);
        for (int y = 0; y < (int) max5Row + 1; y++)
            canvas.drawLine(0, (float) (y * getHeight() / max5Row), getWidth(), (float) (y * getHeight() / max5Row), paint);

//        int leftColumn = (int)Math.round(-gridOrigin[0] / squareSize);
//        int rightColumn = (int)Math.round(leftColumn + getWidth() / (squareSize)) + 1;
//        for (int row = 0; row < bottomRow; row++) {
//            for (int column = leftColumn; column < rightColumn; column++) {
//                drawSquare(canvas, row, column);
//            }
//        }
    }

    public void drawSquare(Canvas canvas, int row, int column) {

        int topRow = (int) Math.round(-gridOrigin[1] / squareSize);
        int bottomRow = (int) Math.round(topRow + getHeight() / (squareSize)) + 1;

        double x = gridOrigin[0] + column * squareSize + 0.5;
        double y = gridOrigin[1] + row * squareSize + 0.5;
//        context.lineWidth = 1;
//        context.strokeStyle = "red";
//        context.beginPath();
        paint.setColor(getResources().getColor(R.color.cardiacare_color_grid));
        for (int i = 0; i < bottomRow; i++) {
            double lineOffset = i * (ppmm + 1);
            double lineLength = squareSize - 1;
            canvas.drawLine((float) x, (float) (y + lineOffset), (float) (x + lineLength), (float) (y + lineOffset), paint);
//            context.moveTo(x, y + lineOffset);
//            context.lineTo(x + lineLength, y + lineOffset);
            canvas.drawLine((float) (x + lineOffset), (float) y, (float) (x + lineOffset), (float) (y + lineLength), paint);
//            context.moveTo(x + lineOffset, y);
//            context.lineTo(x + lineOffset, y + lineLength);
        }
    }

    public void getECGData(int[] ecg_buffer) {

        double[] sig = new double[ecg_buffer.length];
        for (int i = 0; i < ecg_buffer.length; i++) {
            sig[i] = new Double(ecg_buffer[i]).doubleValue();
            ecgForHeartRate.add(new Double(ecg_buffer[i]).doubleValue());
        }

//        sig = broadband_filter(sig);

        for (int i = 0; i < ecg_buffer.length; i++)
            ecg.add(sig[i]);

        if (ecgForHeartRate.size() > 4000) {
            getRR(ecgForHeartRate);
            ecgForHeartRate.clear();
        }
    }

    public void getRR(LinkedList<Double> ecg) {

        double[] Signal = new double[ecg.size()];
        double[] Signal2 = new double[ecg.size()];

        for (int i = 0; i < ecg.size(); i++) {
            Signal[i] = ecg.get(i); // Watch out for NullPointerExceptions!
        }

        Signal = pan_t_deriv1(Signal);
        Signal2 = pan_t_deriv1(Signal);
        Signal = signal_sum(Signal, Signal2);
        Signal = signal_square(Signal);
        Signal = broadband_filter(Signal);

//		 String s = "";
//		 for (int i=0;i < points1.length;i++){
//			 s+= points1[i]+" ";
//		 }
//		Log.i("TAG","double buf:"+s);


        double sum = 0;
        for (int i = 0; i < Signal.length; i++) {
            sum += Signal[i];
        }
        double avg_point = sum / Signal.length;

        double max = 0;
        for (int i = 0; i < Signal.length; i++) {
            if (Signal[i] > max)
                max = Signal[i];
        }

        double threshold = max / 5;
        int j = 0;
        double MaxPoint;
        int MaxIndex, seriesFirst, seriesLast;

        LinkedList<Integer> Peaks, RPeaks;

        Peaks = new LinkedList<Integer>();
        RPeaks = new LinkedList<Integer>();

        for (int i = 0; i < Signal.length; i++) {
            if (Signal[i] > threshold) {
                Peaks.add(i);
//                cout <<i<<"\n";
//                seriafile<<i<<"\n";
            }
        }

        // Find a local max
        while (j < Peaks.size()) {
            seriesFirst = j;
            seriesLast = j;

            if (seriesLast < Peaks.size() - 1) {
                // Search for series of points indexes above THRESHOLD
                while ((Peaks.get(seriesLast) == (Peaks.get(seriesLast + 1) - 1)) & (seriesLast < (Peaks.size() - 2))) {
                    seriesLast++;
                }
                // Search peaks
                if (seriesLast > seriesFirst) {
                    // Search local max
                    MaxPoint = Signal[seriesFirst];
                    MaxIndex = seriesFirst;
                    for (int k = seriesFirst; k <= seriesLast; k++) {
                        if (Signal[Peaks.get(k)] > MaxPoint) {
                            MaxPoint = Signal[Peaks.get(k)];
                            MaxIndex = k;
                        }
                    }

                    RPeaks.add(Peaks.get(MaxIndex));
//	                qDebug() << Peaks[MaxIndex] <<" peak\n";
//                    cout <<  Peaks[MaxIndex] <<" peak\n";
                } else {
                    MaxPoint = Signal[seriesFirst];
                    MaxIndex = seriesFirst;
                    RPeaks.add(Peaks.get(MaxIndex));
//	              qDebug() << Peaks[MaxIndex] <<" peak\n";
//                    cout <<  Peaks[MaxIndex] <<" peak\n";
                }
            }
            j = seriesLast + 1;
        }


        // Search RR-intervals
        for (int i = 0; i < RPeaks.size() - 1; i++) {
            double rr = (RPeaks.get(i + 1) - RPeaks.get(i)) / 300.0;
            RRIntervals.add(rr);
//            cout << rr <<" RR-interval\n";
//            Log.i("TAG","RR-interval " + rr);
        }
    }

    double[] pan_t_deriv1(double[] Signal) {
        double[] sig = new double[Signal.length];

        sig[0] = 0.0f;
        sig[1] = 0.0f;
        sig[2] = 0.0f;
        sig[3] = 0.0f;

        for (int i = 4; i < Signal.length; i++) {
            double numb = 0.125f * (2 * Signal[i] + Signal[i - 1] - Signal[i - 3] - 2 * Signal[i - 4]);
            sig[i] = numb;
        }
        return sig;
    }

    public double[] signal_sum(double[] Signal1, double[] Signal2) {
        double[] sig = new double[Signal1.length];
        for (int i = 0; i < Signal1.length - 1; i++) {
            double numb = Signal1[i] + Signal2[i];
            sig[i] = numb;
        }
        return sig;
    }

    public double[] signal_square(double[] Signal) {
        double[] sig = new double[Signal.length];
        for (int i = 0; i < Signal.length; i++) {
            double numb = (double) Math.pow(Signal[i], 2);
            sig[i] = numb;
        }
        return sig;
    }

    public double[] broadband_filter(double[] Signal) {
        double[] sig = new double[Signal.length];

//        sig[0] = Signal[0];
        for (int i = 0; i < Signal.length; i++) {
            double numb = 0.0f;

            if (i < 8) {
                for (int j = 0; j < i + 1; j++)
                    numb += Signal[j];
                numb /= i + 1;
            } else {
                for (int j = i; j > i - 8; j--)
                    numb += Signal[j];
                numb /= 8;
            }
            sig[i] = numb;
        }
        return sig;
    }
}
