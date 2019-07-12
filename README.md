# MVP
android应用MVP架构实现

## 功能

```kotlin

runOnLifeCycles(AddStrategy.INSERT_TAIL, LifeCycle.STOP).scrollTo(0)

```

让View的方法在指定的声明周期内执行，如果没有设置View则会在设置View后，并且View已经处于指定的生命周期方法后调用

```kotlin

presenter.loadData()

```

可以在bindPresenter之前就调用Presenter的方法而不会导致空指针，并且该方法会在Presenter被设置后执行

更多的功能及实现见Sample
