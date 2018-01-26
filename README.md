# Graph Views

## Introduction

Two libraries to create Line Graph and Pie Chart Views easily.

## How To Include

### Including the library
You can include the dependency using JitPack, directions provided on the page:


--or--

If you already have the JitPack repository included in your root gradle file, just add the following line to your dependencies:


## Line Graphs


#### Creating a Line Graph

1. Create a container that will hold the line graph. 

    ```
    FrameLayout container = findViewsById(R.id.container);
    ```

2. Pass the values to be plot to the LineGraphView.Builder class. 
3. Set the Line Color and Fill Color that you want the graph to use. You can choose to not add your own colors, in which case the fill color defaults to a transparent blue and line color to a dark gray.
4. Choose whether you want axes to be displayed. Defaults to no axes displayed.
5. Choose whether you want the line graph to be progressive (The values accumulate along the x-axis). Defaults to an instance based graph (Each value is treated as it's own value when plotting)

    ```
    LineGraphView.Builder lineGraphBuilder = new LineGraphView.Builder(dummyData)
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
        
#### Altering Graph after creation

The LineGraphClass has setters and getters to let you check and change:
 + The plotted values,
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


#### Creating a Pie Chart

1. Create a container that will hold the line graph. 

    ```
    FrameLayout container = findViewsById(R.id.container);
    ```

2. Call the PieChartView constructor directly. It takes a context, a List of Floats as its values, and a list of the colors to be used to designate the different values. (You can pass null to the color list in which case it defaults to using a grey scale)

3. Add the newly created view to the container.

```
        mPieChartView = new PieChartView(this, createRandomDummyData(),null);
        mContainer.addView(mPieChartView);

```
