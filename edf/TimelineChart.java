package edf;

import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class TimelineChart extends XYChart<Number, String> {

    TimelineChart(Axis<Number> numberAxis, Axis<String> stringAxis) {
        super(numberAxis, stringAxis);
    }

    @Override
    protected void dataItemAdded(Series<Number, String> series, int itemIndex, Data<Number, String> item) {

        Node block = createContainer(series, getData().indexOf(series), item, itemIndex);
        getPlotChildren().add(block);
    }

    @Override
    protected void dataItemRemoved(Data<Number, String> item, Series<Number, String> series) {

        final Node block = item.getNode();
        getPlotChildren().remove(block);
        removeDataItemFromDisplay(series, item);
    }

    @Override
    protected void dataItemChanged(Data<Number, String> item) {

    }

    @Override
    protected void seriesAdded(Series<Number, String> series, int seriesIndex) {

        for (int j=0; j< series.getData().size(); j++) {
            Data<Number,String> item = series.getData().get(j);
            Node container = createContainer(series, seriesIndex, item, j);
            getPlotChildren().add(container);
        }
    }

    @Override
    protected void seriesRemoved(Series<Number, String> series) {

        for (XYChart.Data<Number,String> d : series.getData()) {
            final Node container = d.getNode();
            getPlotChildren().remove(container);
        }
        removeSeriesFromDisplay(series);

    }

    @Override
    protected void layoutPlotChildren() {

        for (int seriesIndex=0; seriesIndex < getData().size(); seriesIndex++) {

            Series<Number, String> series = getData().get(seriesIndex);

            Iterator<Data<Number, String>> iter = getDisplayedDataIterator(series);
            while(iter.hasNext()) {
                Data<Number, String> item = iter.next();
                double x = getXAxis().getDisplayPosition(item.getXValue());
                double y = getYAxis().getDisplayPosition(item.getYValue());
                if (Double.isNaN(x) || Double.isNaN(y)) {
                    continue;
                }
                Node block = item.getNode();
                Rectangle rectangle;
                if (block != null) {
                    if (block instanceof StackPane) {
                        StackPane region = (StackPane)item.getNode();
                        if (region.getShape() == null) {
                            rectangle = new Rectangle(getLength(), getBlockHeight());
                        } else if (region.getShape() instanceof Rectangle) {
                            rectangle = (Rectangle)region.getShape();
                        } else {
                            return;
                        }
                        rectangle.setWidth(getLength());
                        rectangle.setHeight(getBlockHeight());
                        rectangle.setArcHeight(getRadius());
                        rectangle.setArcWidth(getRadius());
                        y -= getBlockHeight() / 2.0;

                        // Note: workaround for RT-7689 - saw this in ProgressControlSkin
                        // The region doesn't update itself when the shape is mutated in place, so we
                        // null out and then restore the shape in order to force invalidation.
                        region.setShape(null);
                        region.setShape(rectangle);
                        region.setScaleShape(false);
                        region.setCenterShape(false);
                        region.setCacheShape(false);

                        block.setLayoutX(x);
                        block.setLayoutY(y);
                    }
                }
            }
        }
    }

    @Override
    protected void updateAxisRange() {
        final Axis<Number> xa = getXAxis();
        final Axis<String> ya = getYAxis();
        List<Number> xData = null;
        List<String> yData = null;
        if(xa.isAutoRanging()) xData = new ArrayList<Number>();
        if(ya.isAutoRanging()) yData = new ArrayList<String>();
        if(xData != null || yData != null) {
            for(Series<Number, String> series : getData()) {
                for(Data<Number, String> data: series.getData()) {
                    if(xData != null) {
                        xData.add(data.getXValue());
                        xData.add(xa.toRealValue(xa.toNumericValue(data.getXValue()) + getLength()));
                    }
                    if(yData != null){
                        yData.add(data.getYValue());
                    }
                }
            }
            if(xData != null) xa.invalidateRange(xData);
            if(yData != null) ya.invalidateRange(yData);
        }
    }

    private static String getStyleClass( Object obj) {
        return ((ExtraData) obj).getStyleClass();
    }

    private double getBlockHeight() {
        return  25.0;
    }

    private double getLength() {
        NumberAxis axis = (NumberAxis)this.getXAxis();
        return axis.getScale();
    }

    private double getRadius() {
        return  12.0;
    }

    private Node createContainer(Series<Number, String> series, int seriesIndex, final Data<Number,String> item, int itemIndex) {

        Node container = item.getNode();

        if (container == null) {
            container = new StackPane();
            item.setNode(container);
        }

        container.getStyleClass().add( getStyleClass(item.getExtraValue()));

        return container;
    }


    static class ExtraData {

        private String styleClass;

        ExtraData(String styleClass) {
            super();
            this.styleClass = styleClass;
        }
        String getStyleClass() {
            return styleClass;
        }
    }

}
