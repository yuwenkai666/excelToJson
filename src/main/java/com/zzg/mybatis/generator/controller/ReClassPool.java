package com.zzg.mybatis.generator.controller;

import javassist.ClassPool;
import javassist.CtClass;

public class ReClassPool extends ClassPool{
	
	
	public ReClassPool() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ReClassPool(boolean useDefaultPath) {
		super(useDefaultPath);
		// TODO Auto-generated constructor stub
	}

	public ReClassPool(ClassPool parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	@Override
    public CtClass removeCached(String classname) {
        return (CtClass)classes.remove(classname);
    }
	
	public ClassPool getClassPool() {
		return ClassPool.getDefault();
		
	}
}
