/**
 * 
 */
package it.unical.mat.smart_playground.controller;

import java.util.List;

import it.unical.mat.smart_playground.controller.playground.PlaygroundController;
import it.unical.mat.smart_playground.model.ecosystem.EcosystemStatus;
import it.unical.mat.smart_playground.model.ecosystem.MotionControllerStatus;
import it.unical.mat.smart_playground.model.ecosystem.SmartBallStatus;
import it.unical.mat.smart_playground.model.ecosystem.SmartFieldStatus;
import it.unical.mat.smart_playground.model.ecosystem.SmartRacketStatus;
import it.unical.mat.smart_playground.model.ecosystem.SmartRacketType;
import it.unical.mat.smart_playground.model.ecosystem.TelosbBasedStatus;
import it.unical.mat.smart_playground.model.ecosystem.WindDirection;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatus;
import it.unical.mat.smart_playground.network.BallTrackingMotionCtrlCommProvider;
import it.unical.mat.smart_playground.view.Strings;
import it.unical.mat.smart_playground.view.animation.WindFlagAnimationManager;
import it.unical.mat.smart_playground.view.animation.WindSpeedAnimationManager;
import it.unical.mat.smart_playground.view.animation.WindSpeedAnimator;
import it.unical.mat.smart_playground.view.playground.PlaygroundField;
import it.unical.mat.smart_playground.view.widget.BallOrientationTileController;
import it.unical.mat.smart_playground.view.widget.LightsFansCtrlTileController;
import it.unical.mat.smart_playground.view.widget.PlayerOrientationTileController;
import it.unical.mat.smart_playground.view.widget.WindDirectionTileController;
import it.unical.mat.smart_playground.view.widget.WindSpeedTileController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * @author Agostino
 *
 */
public class EcosystemStatusController implements ViewController
{
	private final double CHARTS_MAX_WIDTH  = 430.0;
	private final double CHARTS_MAX_HEIGHT = 230.0;
	
	
	// smart ball sensors charts.
	
	@FXML
	private CategoryAxis xAxisMainSmartBallTemperatureSensorsChart;
	@FXML
	private CategoryAxis xAxisGatherSmartBallTemperatureSensorsChart;
	
	@FXML
	private CategoryAxis xAxisMainSmartBallHumiditySensorsChart;
	@FXML
	private CategoryAxis xAxisGatherSmartBallHumiditySensorsChart;
	
	@FXML
	private CategoryAxis xAxisMainSmartBallBrightnessSensorsChart;
	@FXML
	private CategoryAxis xAxisGatherSmartBallBrightnessSensorsChart;
	
	@FXML
	private AreaChart< String, Double > mainSmartBallTemperatureSensorsChart;
	@FXML
	private AreaChart< String, Double > gatherSmartBallTemperatureSensorsChart;
	
	@FXML
	private AreaChart< String, Double > mainSmartBallHumiditySensorsChart;
	@FXML
	private AreaChart< String, Double > gatherSmartBallHumiditySensorsChart;
	
	@FXML
	private AreaChart< String, Double > mainSmartBallBrightnessSensorChart;
	@FXML
	private AreaChart< String, Double > gatherSmartBallBrightnessSensorChart;
	
	
	// smart field sensors charts.
	
	@FXML
	private AreaChart< String, Double > mainSmartFieldTemperatureSensorsChart;
	@FXML
	private AreaChart< String, Double > gatherSmartFieldTemperatureSensorsChart;
	
	@FXML
	private AreaChart< String, Double > mainSmartFieldHumiditySensorsChart;
	@FXML
	private AreaChart< String, Double > gatherSmartFieldHumiditySensorsChart;
	
	@FXML
	private AreaChart< String, Double > mainSmartFieldBrightnessSensorChart;
	@FXML
	private AreaChart< String, Double > gatherSmartFieldBrightnessSensorChart;
	
	
	// smartfield/smartball average sensors charts.
	
	@FXML
	private AreaChart< String, Double > averageTemperatureSensorsChart;
	@FXML
	private AreaChart< String, Double > averageHumiditySensorsChart;
	@FXML
	private AreaChart< String, Double > averageBrightnessSensorChart;

	
	// player motion controller charts.
	
	@FXML private Parent playerOrientationTile;
	// TODO: add PlayerOrientationController, even in fxml file
	
	
	// main smart racket chart.
	
	@FXML
	private CategoryAxis xAxisMainRacketXAccelerometerSensorsChart;
	@FXML
	private CategoryAxis xAxisMainRacketYAccelerometerSensorsChart;
	@FXML
	private CategoryAxis xAxisMainRacketZAccelerometerSensorsChart;
	
	@FXML
	private AreaChart< String, Double > mainRacketXAccelerometerSensorsChart;
	@FXML
	private AreaChart< String, Double > mainRacketYAccelerometerSensorsChart;
	@FXML
	private AreaChart< String, Double > mainRacketZAccelerometerSensorsChart;
	
	
	// main smart club chart.
	
	@FXML
	private CategoryAxis xAxisClubXAccelerometerSensorsChart;
	@FXML
	private CategoryAxis xAxisClubYAccelerometerSensorsChart;
	@FXML
	private CategoryAxis xAxisClubZAccelerometerSensorsChart;
	@FXML
	private AreaChart< String, Double > mainClubXAccelerometerSensorsChart;
	@FXML
	private AreaChart< String, Double > mainClubYAccelerometerSensorsChart;
	@FXML
	private AreaChart< String, Double > mainClubZAccelerometerSensorsChart;
	
	
	@FXML
	private Label gamePlatformDescription;
	@FXML
	private HBox gamePlatformDataBox;
	@FXML
	private HBox ballDataBox;
	@FXML
	private Label temperatureLabel;
	@FXML
	private Label humidityLabel;
	@FXML
	private Label brightnessLabel;
	@FXML
	private Label windDirectionLabel;
	
	@FXML
	private Canvas playgroundFieldCanvas;
	@FXML
	private ImageView playgroundFieldBallImage;
	
	@FXML
	private ImageView windFlagImage0;
	@FXML
	private ImageView windFlagImage1;
	@FXML
	private ImageView windFlagImage2;
	@FXML
	private ImageView windFlagImage3;
	
	@FXML private Parent mainWindDirectionTile;
	@FXML private WindDirectionTileController mainWindDirectionTileController;
	@FXML private Parent secondWindDirectionTile;
	@FXML private WindDirectionTileController secondWindDirectionTileController;
	
	@FXML private Parent mainWindSpeedTile;
	@FXML private WindSpeedTileController mainWindSpeedTileController;
	@FXML private Parent secondWindSpeedTile;
	@FXML private WindSpeedTileController secondWindSpeedTileController;
	
	@FXML private Parent ballOrientationTile;
	@FXML private BallOrientationTileController ballOrientationTileController;
	
	@FXML private Parent lightsFansCrtlTile;
	@FXML private LightsFansCtrlTileController lightsFansCtrlTileController;
	
	@FXML private Canvas windFanCanvas;
	
	private MainApplication mainApp=null;
	private Node content=null;
	
	private PlaygroundField mainPlaygroundField;
	
	
	@Override
	public void init(MainApplication app, Node content)
	{
		this.mainApp = app;
		this.content = content;
		
		mainPlaygroundField = new PlaygroundField(playgroundFieldCanvas, playgroundFieldBallImage);
		
		PlaygroundStatus.getInstance().addObserver(mainPlaygroundField);
		
		final WindFlagAnimationManager windFlagAnimator = WindFlagAnimationManager.getInstance();
		windFlagAnimator.addAnimation(windFlagImage0);
		windFlagAnimator.addAnimation(windFlagImage1);
		windFlagAnimator.addAnimation(windFlagImage2);
		windFlagAnimator.addAnimation(windFlagImage3);
		
		mainWindDirectionTileController.onInitialize(null);
		secondWindDirectionTileController.onInitialize(null);
		
		mainWindSpeedTileController.onInitialize(null);
		secondWindSpeedTileController.onInitialize(null);
		
		ballOrientationTileController.onInitialize(null);
		
		//lightsFansCtrlTileController.onInitialize(null);
		
		initCharts();
		
		/*
		gamePlatformDataBox.getChildren().add(gamePlatformTemperatureSensorsChart);
		gamePlatformDataBox.getChildren().add(gamePlatformBrightnessSensorChart);
		
		ballDataBox.getChildren().add(smartBallTemperatureSensorsChart);
		ballDataBox.getChildren().add(smartBallBrightnessSensorChart);
		
		motionControllerDataBox.getChildren().add(motionControllerPlayerDirectionChart);
		
		smartPoleDataBox.getChildren().add(smartPoleWindDirectionChart);
		smartPoleDataBox.getChildren().add(smartPoleTemperatureSensorsChart);
		smartPoleDataBox.getChildren().add(smartPoleBrightnessSensorChart);
		*/
	}

	@Override
	public void fin()
	{		
		PlaygroundStatus.getInstance().removeObserver(mainPlaygroundField);
		
		final WindFlagAnimationManager windFlagAnimator = WindFlagAnimationManager.getInstance();
		windFlagAnimator.removeAnimation(windFlagImage0);
		windFlagAnimator.removeAnimation(windFlagImage1);
		windFlagAnimator.removeAnimation(windFlagImage2);
		windFlagAnimator.removeAnimation(windFlagImage3);
		
		mainWindDirectionTileController.onFinalize();
		secondWindDirectionTileController.onFinalize();
		
		mainWindSpeedTileController.onFinalize();
		secondWindSpeedTileController.onFinalize();		
		
		ballOrientationTileController.onFinalize();
		
		lightsFansCtrlTileController.onFinalize();
	}

	@Override
	public MainApplication getMainApp()
	{
		return mainApp;
	}

	@Override
	public Node getContent()
	{
		return content;
	}
	
	public void onSmartBallStatus()
	{
		updateLightTelosbBasedStatus
		( EcosystemStatus.getInstance().getSmartBallStatus(), "Smart Ball",
				mainSmartBallTemperatureSensorsChart, mainSmartBallHumiditySensorsChart, mainSmartBallBrightnessSensorChart,
				gatherSmartBallTemperatureSensorsChart, gatherSmartBallHumiditySensorsChart, gatherSmartBallBrightnessSensorChart,
				averageTemperatureSensorsChart, averageHumiditySensorsChart, averageBrightnessSensorChart,
				temperatureLabel, humidityLabel, brightnessLabel );
	}
	
	public void onSmartFieldStatus()
	{
		updateLightTelosbBasedStatus
		( EcosystemStatus.getInstance().getSmartFieldStatus(), "Smart Field",
				mainSmartFieldTemperatureSensorsChart, mainSmartFieldHumiditySensorsChart, mainSmartFieldBrightnessSensorChart,
				gatherSmartFieldTemperatureSensorsChart, gatherSmartFieldHumiditySensorsChart, gatherSmartFieldBrightnessSensorChart,
				averageTemperatureSensorsChart, averageHumiditySensorsChart, averageBrightnessSensorChart,
				temperatureLabel, humidityLabel, brightnessLabel );
	}
	
	public void onSmartRacketStatus( final SmartRacketType smartRacket )
	{
		switch ( smartRacket )
		{
		case MAIN: 
			onMainSmartRacketStatus();
			break;
		case SECOND: 
			// TODO: updateSmartRacketStatus( EcosystemStatus.getInstance().getSecondSmartRacketStatus(), secondRacketAccelerometerSensorsChart );
			break;
		}
	}
	
	public void onMainSmartRacketStatus()
	{
		final SmartRacketStatus mainSmartRacketStatus = EcosystemStatus.getInstance().getMainSmartRacketStatus();
		
		updateSmartRacketStatus( mainSmartRacketStatus, 
				mainRacketXAccelerometerSensorsChart, mainRacketYAccelerometerSensorsChart, mainRacketZAccelerometerSensorsChart );
		
		updateSmartRacketStatus( mainSmartRacketStatus, 
				mainClubXAccelerometerSensorsChart, mainClubYAccelerometerSensorsChart, mainClubZAccelerometerSensorsChart );
	}
	
	public void onScale( final double scale_factor )
	{
		//scale.setX(scale_factor);
		//scale.setY(scale_factor);	\
		content.setScaleX(scale_factor);
		content.setScaleY(scale_factor);
	}
	
	@Override
	public void updateAnimation(long now)
	{
		
	}
	
	private static void updateTelosbBasedStatus( final TelosbBasedStatus           status,
												 final String                      sensorName,
												 final AreaChart< String, Double > mainTemperatureChart,
												 final AreaChart< String, Double > mainHumidityChart,
												 final AreaChart< String, Double > mainBrightnessChart,
												 final AreaChart< String, Double > gatherTemperatureChart,
												 final AreaChart< String, Double > gatherHumidityChart,
												 final AreaChart< String, Double > gatherBrightnessChart )
	{
		final XYChart.Series<String, Double> seriesTemperature        = new XYChart.Series<>();
		final XYChart.Series<String, Double> seriesGatherTemperature  = new XYChart.Series<>();
		final XYChart.Series<String, Double> seriesTemperatureAverage = new XYChart.Series<>();
		
		final XYChart.Series<String, Double> seriesHumidity           = new XYChart.Series<>();
		final XYChart.Series<String, Double> seriesGatherHumidity     = new XYChart.Series<>();
		final XYChart.Series<String, Double> seriesHumidityAverage    = new XYChart.Series<>();
		
		final XYChart.Series<String, Double> seriesBrightness         = new XYChart.Series<>();
		final XYChart.Series<String, Double> seriesGatherBrightness   = new XYChart.Series<>();
		final XYChart.Series<String, Double> seriesBrightnessAverage  = new XYChart.Series<>();
		
		seriesTemperature.setName("Sensor (°C)");
		seriesGatherTemperature.setName(sensorName);
		seriesTemperatureAverage.setName("Avg (*" + EcosystemStatus.TEMPERATURE_AVERAGE_VALUE_RATIO + " °C)");
		
		seriesHumidity.setName("Sensor");
		seriesGatherHumidity.setName(sensorName);
		seriesHumidityAverage.setName("Average");
		
		seriesBrightness.setName("Sensor");
		seriesGatherBrightness.setName(sensorName);
		seriesBrightnessAverage.setName("Average");
		
		final List< Integer > temperatureValues = status.getTemperatureValues();
		final List< Integer > humidityValues = status.getHumidityValues();
		final List< Integer > brightnessValues = status.getBrightnessValues();
		
		final EcosystemStatus ecosystemStatus = EcosystemStatus.getInstance();
		
		final List< Integer > temperatureAverageValues = ecosystemStatus.getTemperatureAverageValues( temperatureValues.size() );
		final List< Integer > humidityAverageValues = ecosystemStatus.getHumidityAverageValues( humidityValues.size() );
		final List< Integer > brightnessAverageValues = ecosystemStatus.getBrightnessAverageValues( brightnessValues.size() );
		
		
		updateDataCharts( temperatureValues, seriesTemperature, seriesTemperatureAverage, 
						  temperatureAverageValues, EcosystemStatus.TEMPERATURE_AVERAGE_VALUE_RATIO );
		
		updateDataCharts( humidityValues, seriesHumidity, seriesHumidityAverage, 
						  humidityAverageValues, EcosystemStatus.HUMIDITY_AVERAGE_VALUE_RATIO );
		
		updateDataCharts( brightnessValues, seriesBrightness, seriesBrightnessAverage, 
						  brightnessAverageValues, EcosystemStatus.BRIGHTNESS_AVERAGE_VALUE_RATIO );
		
		
		updateDataCharts( temperatureValues, seriesGatherTemperature, seriesTemperatureAverage, 
				  temperatureAverageValues, EcosystemStatus.TEMPERATURE_AVERAGE_VALUE_RATIO );

		updateDataCharts( humidityValues, seriesGatherHumidity, seriesHumidityAverage, 
				  humidityAverageValues, EcosystemStatus.HUMIDITY_AVERAGE_VALUE_RATIO );

		updateDataCharts( brightnessValues, seriesGatherBrightness, seriesBrightnessAverage, 
				  brightnessAverageValues, EcosystemStatus.BRIGHTNESS_AVERAGE_VALUE_RATIO );


		updateChartWithData( mainTemperatureChart, seriesTemperature, seriesTemperatureAverage );
		updateChartWithData( mainHumidityChart, seriesHumidity, seriesHumidityAverage );
		updateChartWithData( mainBrightnessChart, seriesBrightness, seriesBrightnessAverage );
		
		updateChartWithData( gatherTemperatureChart, seriesGatherTemperature, seriesTemperatureAverage );
		updateChartWithData( gatherHumidityChart, seriesGatherHumidity, seriesHumidityAverage );
		updateChartWithData( gatherBrightnessChart, seriesGatherBrightness, seriesBrightnessAverage );
	}
	
	private static void updateLightTelosbBasedStatus( final TelosbBasedStatus           status,
													  final String                      sensorName,
													  final AreaChart< String, Double > mainTemperatureChart,
													  final AreaChart< String, Double > mainHumidityChart,
													  final AreaChart< String, Double > mainBrightnessChart,
													  final AreaChart< String, Double > gatherTemperatureChart,
													  final AreaChart< String, Double > gatherHumidityChart,
													  final AreaChart< String, Double > gatherBrightnessChart,
													  final AreaChart< String, Double > averageTemperatureChart,
													  final AreaChart< String, Double > averageHumidityChart,
													  final AreaChart< String, Double > averageBrightnessChart,
													  final Label                       temperatureLabel,
													  final Label                       humidityLabel,
													  final Label                       brightnessLabel )
	{
		final XYChart.Series<String, Double> seriesTemperature        = new XYChart.Series<>();
		final XYChart.Series<String, Double> seriesGatherTemperature  = new XYChart.Series<>();
		final XYChart.Series<String, Double> seriesTemperatureAverage = new XYChart.Series<>();
		
		final XYChart.Series<String, Double> seriesHumidity           = new XYChart.Series<>();
		final XYChart.Series<String, Double> seriesGatherHumidity     = new XYChart.Series<>();
		final XYChart.Series<String, Double> seriesHumidityAverage    = new XYChart.Series<>();
		
		final XYChart.Series<String, Double> seriesBrightness         = new XYChart.Series<>();
		final XYChart.Series<String, Double> seriesGatherBrightness   = new XYChart.Series<>();
		final XYChart.Series<String, Double> seriesBrightnessAverage  = new XYChart.Series<>();
		
		seriesTemperature.setName("Temperature (" + Strings.TEMPERATURE_UNIT + ")");
		seriesGatherTemperature.setName(sensorName + " Sensor (" + Strings.TEMPERATURE_UNIT + ")");
		seriesTemperatureAverage.setName("Average (" + Strings.TEMPERATURE_UNIT + ")");
		
		seriesHumidity.setName("Humidity (" + Strings.HUMIDITY_UNIT + ")");
		seriesGatherHumidity.setName(sensorName + " Sensor (" + Strings.HUMIDITY_UNIT + ")");
		seriesHumidityAverage.setName("Average (" + Strings.HUMIDITY_UNIT + ")");
		
		seriesBrightness.setName("Brightness (" + Strings.BRIGHTNESS_UNIT + ")");
		seriesGatherBrightness.setName(sensorName + " Sensor (" + Strings.BRIGHTNESS_UNIT + ")");
		seriesBrightnessAverage.setName("Average (" + Strings.BRIGHTNESS_UNIT + ")");
		
		final List< Integer > temperatureValues = status.getTemperatureValues();
		final List< Integer > humidityValues = status.getHumidityValues();
		final List< Integer > brightnessValues = status.getBrightnessValues();
		
		final EcosystemStatus ecosystemStatus = EcosystemStatus.getInstance();
		
		final List< Integer > temperatureAverageValues = ecosystemStatus.getTemperatureAverageValues( temperatureValues.size() );
		final List< Integer > humidityAverageValues = ecosystemStatus.getHumidityAverageValues( humidityValues.size() );
		final List< Integer > brightnessAverageValues = ecosystemStatus.getBrightnessAverageValues( brightnessValues.size() );
		
		updateLightDataCharts( temperatureValues, seriesTemperature );
		updateLightDataCharts( humidityValues, seriesHumidity );
		updateLightDataCharts( brightnessValues, seriesBrightness );
		
		
		updateLightDataCharts( temperatureValues, seriesGatherTemperature );
		updateLightDataCharts( humidityValues, seriesGatherHumidity );
		updateLightDataCharts( brightnessValues, seriesGatherBrightness );
		
		
		updateLightDataCharts( temperatureAverageValues, seriesTemperatureAverage );
		updateLightDataCharts( humidityAverageValues, seriesHumidityAverage );
		updateLightDataCharts( brightnessAverageValues, seriesBrightnessAverage );
		
		
		updateLightChartWithData( mainTemperatureChart, seriesTemperature );
		updateLightChartWithData( mainHumidityChart, seriesHumidity );
		updateLightChartWithData( mainBrightnessChart, seriesBrightness );
		
		updateLightChartWithData( gatherTemperatureChart, seriesGatherTemperature );
		updateLightChartWithData( gatherHumidityChart, seriesGatherHumidity );
		updateLightChartWithData( gatherBrightnessChart, seriesGatherBrightness );
		
		updateLightChartWithData( averageTemperatureChart, seriesTemperatureAverage );
		updateLightChartWithData( averageHumidityChart, seriesHumidityAverage );
		updateLightChartWithData( averageBrightnessChart, seriesBrightnessAverage );
		
		
		updateSensorValueLabel( temperatureLabel, temperatureAverageValues, Strings.TEMPERATURE_UNIT );
		updateSensorValueLabel( humidityLabel, humidityAverageValues, Strings.HUMIDITY_UNIT );
		updateSensorValueLabel( brightnessLabel, brightnessAverageValues, Strings.BRIGHTNESS_UNIT );
	}
	
	private static void updateSmartRacketStatus( final SmartRacketStatus           status,
												 final AreaChart< String, Double > xAccelerometerChart,
												 final AreaChart< String, Double > yAccelerometerChart,
												 final AreaChart< String, Double > zAccelerometerChart )
	{		
		final XYChart.Series<String, Double> seriesAccX = getChartSeries( xAccelerometerChart, 0 );
		final XYChart.Series<String, Double> seriesAccY = getChartSeries( yAccelerometerChart, 0 );
		final XYChart.Series<String, Double> seriesAccZ = getChartSeries( zAccelerometerChart, 0 );
		
		seriesAccX.getData().clear();
		seriesAccY.getData().clear();
		seriesAccZ.getData().clear();
				
		seriesAccX.setName("x-axis accelerometer");
		seriesAccY.setName("y-axis accelerometer");
		seriesAccZ.setName("z-axis accelerometer");
		
		final List< Integer > accXValues = status.getAccXValues();
		final List< Integer > accYValues = status.getAccYValues();
		final List< Integer > accZValues = status.getAccZValues();
		
		if ( accXValues.size() == accYValues.size() && accYValues.size() == accZValues.size() )
		{
			final int values_size = accXValues.size();
			
			for( int i=0; i<values_size; ++i )
			{
				seriesAccX.getData().add( new XYChart.Data<>(Integer.toString(i+1), new Double(accXValues.get(i))) );
				seriesAccY.getData().add( new XYChart.Data<>(Integer.toString(i+1), new Double(accYValues.get(i))) );
				seriesAccZ.getData().add( new XYChart.Data<>(Integer.toString(i+1), new Double(accZValues.get(i))) );
			}
		}
		
		updateChartSeries( xAccelerometerChart, seriesAccX );
		updateChartSeries( yAccelerometerChart, seriesAccY );
		updateChartSeries( zAccelerometerChart, seriesAccZ );
	}
	
	private static void updateDataCharts( final List< Integer >                values,
										  final XYChart.Series<String, Double> series,
										  final XYChart.Series<String, Double> seriesAverage,
										  final List< Integer >                averageValues,
										  final double                         averageValueRatio )
	{
		int values_size = values.size();
		int i;
		
		for( i=0; i<values_size; ++i )
		{
			series.getData().add(new XYChart.Data<>(Integer.toString(i+1), new Double(values.get(i))));
			seriesAverage.getData().add
				(new XYChart.Data<>(Integer.toString(i+1), averageValues.get(i) * averageValueRatio));
		}
	}
	
	private static void updateLightDataCharts( final List< Integer >                values,
											   final XYChart.Series<String, Double> series )
	{
		int values_size = values.size();
		int i;
		
		for( i=0; i<values_size; ++i )
			series.getData().add(new XYChart.Data<>(Integer.toString(i+1), new Double(values.get(i))));
	}
	
	private static void updateChartWithData( final AreaChart< String, Double >    chart,
											 final XYChart.Series<String, Double> series,
											 final XYChart.Series<String, Double> seriesAvg )
	{
		if ( chart != null )
		{
			chart.getData().clear();
			chart.getData().add(series);
			chart.getData().add(seriesAvg);
		}
	}
	
	private static void updateLightChartWithData( final AreaChart< String, Double >    chart,
				 							 	  final XYChart.Series<String, Double> series )
	{
		if ( chart != null )
		{
			chart.getData().clear();
			chart.getData().add(series);
		}
	}
	
	private static XYChart.Series<String, Double> getChartSeries( final AreaChart< String, Double > chart, final int data_series_index )
	{
		if ( data_series_index >= chart.getData().size() )
			return new XYChart.Series<>();
		return chart.getData().get(data_series_index);
	}
	
	private static void updateChartSeries( final AreaChart< String, Double > chart, final XYChart.Series<String, Double> seriesAccX )
	{
		if ( chart.getData().isEmpty() )
			chart.getData().add(seriesAccX);
	}
	
	private static void updateSensorValueLabel( final Label valueLabel, final List< Integer > lastValues, final String unit )
	{
		if ( lastValues.isEmpty() )
			valueLabel.setText("--"+unit);
		else
			valueLabel.setText( lastValues.get(lastValues.size()-1) + unit );
	}
	
	private void initCharts()
	{
		/*
		final XYChart.Series<Number, Number> seriesTemp = new XYChart.Series<Number, Number>();
		seriesTemp.setName("Temperature");
		seriesTemp.getData().add(new XYChart.Data(1, 4));
		seriesTemp.getData().add(new XYChart.Data(3, 10));
		seriesTemp.getData().add(new XYChart.Data(4, 12));
		
		final XYChart.Series<Number, Number> seriesBrighteness = new XYChart.Series<Number, Number>();
		seriesBrighteness.setName("Brighteness");
		seriesBrighteness.getData().add(new XYChart.Data(2, 10));
		seriesBrighteness.getData().add(new XYChart.Data(5, 12));
		seriesBrighteness.getData().add(new XYChart.Data(10, 4));
		
		gamePlatformTemperatureSensorsChart.getData().clear();
		gamePlatformTemperatureSensorsChart.getData().add(seriesTemp);
	
		gamePlatformBrightnessSensorChart.getData().clear();
		gamePlatformBrightnessSensorChart.getData().add(seriesBrighteness);
		*/
		
		
		// smart game platform charts.
		
		//gamePlatformTemperatureSensorsChart.setTitle("Temperature");
		//gamePlatformBrightnessSensorChart.setTitle("Brightness");
		
		//gamePlatformTemperatureSensorsChart.setMaxSize(CHARTS_MAX_WIDTH, CHARTS_MAX_HEIGHT);
		//gamePlatformBrightnessSensorChart.setMaxSize(CHARTS_MAX_WIDTH, CHARTS_MAX_HEIGHT);
		
		
		// smart ball charts.
		
		//smartBallTemperatureSensorsChart.setTitle("Temperature");
		//smartBallBrightnessSensorChart.setTitle("Brightness");
		
		//smartBallTemperatureSensorsChart.setMaxSize(CHARTS_MAX_WIDTH, CHARTS_MAX_HEIGHT);
		//smartBallBrightnessSensorChart.setMaxSize(CHARTS_MAX_WIDTH, CHARTS_MAX_HEIGHT);
		
		
		// motion controller charts.
		
		//motionControllerPlayerDirectionChart.setTitle("Player Orientation");
		//motionControllerPlayerDirectionChart.setMaxSize(CHARTS_MAX_WIDTH, CHARTS_MAX_HEIGHT);
		
		
		// smart pole charts.
		
		//smartPoleTemperatureSensorsChart.setTitle("Temperature");
		//smartPoleBrightnessSensorChart.setTitle("Brightness");
		//smartPoleWindDirectionChart.setTitle("Wind Direction");
		
		//smartPoleWindDirectionChart.setMaxSize(CHARTS_MAX_WIDTH, CHARTS_MAX_HEIGHT);
		//smartPoleTemperatureSensorsChart.setMaxSize(CHARTS_MAX_WIDTH, CHARTS_MAX_HEIGHT);
		//smartPoleBrightnessSensorChart.setMaxSize(CHARTS_MAX_WIDTH, CHARTS_MAX_HEIGHT);
		
		//smartPoleWindDirectionChart.setLabelsVisible(false);
		
		
		final String second_chart_style_sheet = getClass().getResource(ControllerConfigs.VIEW_PATH+"second_charts.css").toExternalForm();
		final String third_chart_style_sheet = getClass().getResource(ControllerConfigs.VIEW_PATH+"third_charts.css").toExternalForm();
		
		
		mainSmartBallHumiditySensorsChart.getStylesheets().add( second_chart_style_sheet );
		mainSmartBallBrightnessSensorChart.getStylesheets().add( third_chart_style_sheet );		
		
		gatherSmartBallTemperatureSensorsChart.getStylesheets().add( second_chart_style_sheet );
		averageTemperatureSensorsChart.getStylesheets().add( third_chart_style_sheet );
		
		gatherSmartBallHumiditySensorsChart.getStylesheets().add( second_chart_style_sheet );
		averageHumiditySensorsChart.getStylesheets().add( third_chart_style_sheet );
		
		gatherSmartBallBrightnessSensorChart.getStylesheets().add( second_chart_style_sheet );
		averageBrightnessSensorChart.getStylesheets().add( third_chart_style_sheet );
		
		mainRacketYAccelerometerSensorsChart.getStylesheets().add( second_chart_style_sheet );
		mainRacketZAccelerometerSensorsChart.getStylesheets().add( third_chart_style_sheet );
		
		mainClubYAccelerometerSensorsChart.getStylesheets().add( second_chart_style_sheet );
		mainClubZAccelerometerSensorsChart.getStylesheets().add( third_chart_style_sheet );
		
		mainSmartBallHumiditySensorsChart.getStylesheets().add( second_chart_style_sheet );
		mainSmartBallTemperatureSensorsChart.getStylesheets().add( third_chart_style_sheet );	
	}	
}
