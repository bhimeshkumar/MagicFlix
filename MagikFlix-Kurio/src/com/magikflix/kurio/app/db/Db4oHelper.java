package com.magikflix.kurio.app.db;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.nativequery.example.Student;
import com.magikflix.kurio.app.api.Model.UserProfile;

public class Db4oHelper {

 private static ObjectContainer oc = null;
 private Context context;

 /**
  * @param ctx
  */
 public Db4oHelper(Context ctx) {
  context = ctx;
 }

 /**
  * Create, open and close the database
  */
 public ObjectContainer db() {

  try {
   if (oc == null || oc.ext().isClosed()) {
    oc = Db4oEmbedded.openFile(dbConfig(), db4oDBFullPath(context));
   }

   return oc;

  } catch (Exception ie) {
   Log.e(Db4oHelper.class.getName(), ie.toString());
   return null;
  }
 }

 /**
  * Configure the behavior of the database
  */

 private EmbeddedConfiguration dbConfig() throws IOException {
  EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
  configuration.common().objectClass(Student.class).objectField("name")
    .indexed(true);
  configuration.common().objectClass(Student.class).cascadeOnUpdate(true);
  configuration.common().objectClass(Student.class)
    .cascadeOnActivate(true);
  return configuration;
 }

 /**
  * Returns the path for the database location
  */

 private String db4oDBFullPath(Context ctx) {
  return ctx.getDir("data", 0) + "/" + "magicflix.db4o";
 }

 /**
  * Closes the database
  */

 public void close() {
  if (oc != null)
   oc.close();
 }
 
 
//This method is used to store the object into the database.
	public void store(UserProfile userProfile) {
		db().store(userProfile);
		db().commit();
	}



	//This method is used to retrive all object from database.
	public List<UserProfile> getUserProfiles() {  
		return db().query(UserProfile.class);
	}


	public UserProfile getUserProfileById(int id){

		for (UserProfile userProfile : getUserProfiles()) {
			if(userProfile.id == id){
				return userProfile;
			}

		}
		return null;
	}

	public void delete() {  
		db().delete(UserProfile.class);
	}

	public void deleteProfileById(int mSelectedProfile) {
		  for (UserProfile userProfile : getUserProfiles()) {
			  if(mSelectedProfile == userProfile.id){
				  db().delete(userProfile);
				  db().commit();
				  break;
			  }
			
		}
		
	}

}
