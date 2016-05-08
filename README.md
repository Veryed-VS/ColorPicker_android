# ColorPicker_android
# 这是一个用SurfaceView写的颜色控件
那么我们先上效果图:<br>
![](https://github.com/Veryed-VS/ColorPicker_android/blob/master/image.jpg)
<br>
有何特点:<br>
    现有GitHub的颜色控件项目全部是基于继承View写的。
    我们在从事智能家具行业。所以很多时候使用到颜色控件来控制灯光颜色。但是由于
    触摸点随着手指移动需要不断重绘视图。所以在实际使用中，我们会发现触摸点的移动总是慢半拍，同时可以感觉到，Cpu在使用最快的速
    度进行重绘视图。其实不能使用户达到丝滑般的快感。
    在IOS中我们使用继承UIView进行触摸重绘，CPU使用率达到98%以上，后来使用UIControl控件进行控制控件的移动进行GPU绘图，达到非常丝
    滑的效果。具体项目请查看我另一个IOS项目。
    如今android已经实现。
