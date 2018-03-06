# Graph Views

## Introduction

Two libraries to create Line Graph and Pie Chart Views easily.

## How To Include

### Including the library
You can include the dependency using JitPack, directions provided on the page:

https://jitpack.io/#NikShuvalov/Shoe-Olive-Graphs/v0.2.0-alpha

--or--

If you already have the JitPack repository included in your root gradle file:

To include entire library:
```
    compile 'com.github.NikShuvalov:Shoe-Olive-Graphs:v0.2.0-alpha'
```

To include only Line Graph sub-library:
```
compile 'com.github.NikShuvalov.Shoe-Olive-Graphs:line-graph-view:v0.2.0-alpha'
```

To include only Pie Chart sub-library:
```
compile 'com.github.NikShuvalov.Shoe-Olive-Graphs:pie-chart-view:v0.2.0-alpha'
```

## Line Graphs

## LineGraphable Interface

```
public interface LineGraphable<xAxis extends Number, yAxis extends Number> extends Comparable<LineGraphable> {
    xAxis getXValue();
    yAxis getYValue();
    @Override
    int compareTo(@NonNull LineGraphable lineGraphable);
}
```

The LineGraphView takes a list of Objects with the LineGraphable interface. The xAxis and yAxis value types currently supported only include Integer, Double and Float. The xAxis and yAxis values, as expected, determine where to plot the next point on the linegraph. The LineGraphable needs to be sorted by it's xAxis value to prevent oddities from occuring (Failing to make it sortable will result in the linegraph looking like a pollock painting; while visually interesting, it makes the graph pretty useless).


#### Creating a Line Graph

1. Create a container that will hold the line graph. 

    ```
    FrameLayout container = findViewsById(R.id.container);
    ```

2. Pass the LineGraphables to be plot to the LineGraphView.Builder class. 
3. Set the Line Color and Fill Color that you want the graph to use. You can choose to not add your own colors, in which case the fill color defaults to a transparent blue and line color to a dark gray.
4. Choose whether you want axes to be displayed. Defaults to no axes displayed.
5. Choose whether you want the line graph to be progressive (The values accumulate along the x-axis). Defaults to an instance based graph (Each value is treated as it's own value when plotting)

    ```
    List<LineGraphable> graphables = new ArrayList<>();
    
    ...
    
    LineGraphView.Builder lineGraphBuilder = new LineGraphView.Builder(graphables)
            .setFillColor(Color.rgb(0,0,255))
            .setLineColor(Color.RED)
            .includeAxes()
            .useProgressBased();
            .build(this);
    ```

6. Create the view by passing a context to build().
7. Add the view to a container to display.
    
    ```
    LineGraphView lineGraphView = lineGraphBuilder.build(this);
    container.addView(lineGraphView);
    ````
    
#### Builder Options
| Method| Purpose|
|:---|---|
| setFillColor(int color) | Determines what the fill color of the line graph will be.|
| setLineColor(int color) | Determines what color the line of the line graph will be.|
|includeAxes()| Makes the graph use labels for the x and y-axis. For progress-based graphs, it also includes the final cumulative total on the right hand side of the graph.|
|useProgressBased() | Makes the graph have progressive values; successive values are added to the running total. Omitting this just plots the y-values |
|setAutoX() | Makes the graph ignore the xValues and just plots yValues uniformly along the xAxis|
        
#### Altering Graph after creation

The LineGraphClass has setters and getters to let you check and change:
 + The plotted LineGraphables (setter only)
 + Whether axes are displayed
 + If the graph is/should be progressive (values accumulate) or instance-based (values are shown as is).
 
Same set of values as seen as a instance-based chart and progressive chart, respectively:

<p align="center">
  <img src="https://github.com/NikShuvalov/Shoe-Olive-Graphs/blob/master/instance_line.png" width="300">
  <img src="https://github.com/NikShuvalov/Shoe-Olive-Graphs/blob/master/progressive_line.png" width="300">
</p>


After you've made the desired changes, make sure to call invalidate() on the lineGraphView so that the changes are displayed.

## Pie Charts

<p align="center">
  <img src="https://github.com/NikShuvalov/Shoe-Olive-Graphs/blob/master/pie_graph.png" width="300">
</p>

#### PieGraphable Interface

```
public interface PieGraphable<T extends Number> {
    T getValue();
}
```

The PieGraphView takes a List of Objects with the PieGraphable interface. Even though the Type needs to extend Number, the PieGraphable interface currently only works with Long, Integer, Double or Float values. GetValue() should return the value that determines how much of the PieGraph that Object takes up.

#### Creating a Pie Chart

1. Create a container that will hold the line graph. 

    ```
    FrameLayout container = findViewsById(R.id.container);
    ```

2. Call the PieChartView constructor directly. It takes a context, a List of PieGraphables, and a list of the colors to be used to designate the different values. (You can pass null to the color list in which case it defaults to using a grey scale)

3. Add the newly created view to the container.

```
    List<PieGraphable> graphables = new ArrayList<>();
    ...
    pieChartView = new PieChartView(this, graphables,null);
    container.addView(pieChartView);

```
