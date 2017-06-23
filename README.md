jHighFun
========

***Multithreaded Higher Order Function Library for Java***

## Overview
This library is implememted to make concurrent programming as easy as possible by following best practices exists
and abstracting out the finer interfaces to hide lots of boiler plate code needed in general.

The aim of this library is to help design any kind of business logic which is scalable on multiple CPU's.

 jHighFun => (Higher order functions, function chaining) + (concurrency patterns)

 ***Higher order functions***

 * map
 * filter
 * foldLeft
 * foldRight
 * reduce
 * sort
 * sortWith
 * sortBy
 * any
 * every
 * each
 * eachWithIndex
 * split
 * curry
 * chain
 * memoize


***Concurrency patterns***

 * forkAndJoin
 * divideAndConquer
 * executeAsync
 * executeLater
 * executeWithGlobalLock
 * executeWithLock
 * executeWithPool

Just do static import for FunctionUtil.* into your java file start using them, be careful while using the overloaded methods which accepts "noOfThreads" as last argument and tries to execute function concurrently.

OR

If you are interested in chaining the functions use "chain" function.


Gradle
=====

```

allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
	
	
	
dependencies {
        compile 'com.github.corporatepiyush:jhighfun:1.4'
}

```