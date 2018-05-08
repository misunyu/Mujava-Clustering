package mujava;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class MutantStateInfo {


	 public String _line;
	 public Object _state;
	 public String _name;
	 
	 public MutantStateInfo()
	 {
		 
	 }
	 
	 public MutantStateInfo(String l, String name, Object s)
	 {
		 _line = l;
		 _name = name;
		 _state = s;
	 }
	 
	 public Object getState(){
		 return _state;
	 }
	 
	 public String getLine(){
		 return _line;
	 }
	 
	 /*
	 @Override
	 public boolean equals(Object obj) {
		   return EqualsBuilder.reflectionEquals(state, obj);
	}
	 
	 @Override
     public int hashCode() {
    	  return HashCodeBuilder.reflectionHashCode(this);
     }
	 */
	 public String getName() {
		 return _name;
	 }
	 
	 @Override
     public  String toString() {
    	  return _name;
     }
	 
	 
}
