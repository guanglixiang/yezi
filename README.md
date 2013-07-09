yezi
====

use to store my code

This project use to study Android Contacts develop

2013-07-09:现在在listview的item状态缓存上遇到难题，具体说来当点击某一个item，该item会弹出几个button，此时若滑动listview使得刚才点击的item滚出屏幕的可见区域在滚回来，则会导致弹出button
的item位置发生变化.另外滚动listview，发现有些一开始未可见的item也呈现点击后的状态。分析原因应该是点击item后并未将状态保存，当item出了屏幕在回来时又调用getView方法，而getView方法并不知道
item的改变状态。目前还没想好方法
