package com.eju.cy.drawlibrary.plug;


/**
* @ Name: Caochen
* @ Date: 2019-08-23
* @ Time: 11:30
* @ Description： 被观察者
*/
public interface EjuDrawObservable {


    void addObserver(EjuDrawObserver observer);


    void removeObserver(EjuDrawObserver observer);


    void notifyObservers(Object obj);
}
