package com.magikflix.kurio.app.sectionlistview;


public class SectionItem implements Item{
	 
	 private final String title;
	  
	 public SectionItem(String title) {
	  this.title = title;
	 }
	  
	 public String getTitle(){
	  return title;
	 }
	  
	 @Override
	 public boolean isSection() {
	  return true;
	 }
}