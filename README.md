# MVP

android应用MVP架构实现

## How to

To get a Git project into your build:

### Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```groovy

allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

```

### Step 2. Add the dependency

[![MVP](https://jitpack.io/v/XingRay/MVP.svg)](https://jitpack.io/#XingRay/MVP)

```groovy

dependencies {
    implementation 'com.github.XingRay:MVP:1.1.0'
}

```

## 功能

```kotlin

getLifeCycleView(AddStrategy.INSERT_TAIL, LifeCycle.STOP).scrollTo(0)

runOnLifeCycle(LifeCycle.STOP){
    // do something
}

```

让View的方法在指定的声明周期内执行，如果没有设置View则会在设置View后，并且View已经处于指定的生命周期方法后调用

```kotlin

presenter.loadData()

```

可以在bindPresenter之前就调用Presenter的方法而不会导致空指针，并且该方法会在Presenter被设置后执行

更多的功能及实现见Sample
