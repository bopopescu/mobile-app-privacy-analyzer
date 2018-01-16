package functionalities;

import java.io.IOException;
import java.io.InputStream;

import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import com.google.gson.Gson;

import models.ApplicationPermissionsModel;
import models.LibraryModel;

public class APKAnalyzer {

	public LibraryModel[] getLibrariesPermissions(String apkPath) {
		PythonInterpreter interpreter = new PythonInterpreter();
		interpreter.execfile("mypythonscripts/literadar.py");
		String run="repr(myClass.run(myClass(),'"+apkPath+"'))";
		PyObject str = interpreter.eval(run);
		// System.out.println(str.toString());

		LibraryModel[] libModels = null;
		Gson g = new Gson();
		libModels = g.fromJson(str.toString(), LibraryModel[].class);
		interpreter.close();
		// System.out.println("PRINT "+libModels[0].Library);
		return libModels;
	}

	public ApplicationPermissionsModel getAPKPermissions(String apkPath) throws IOException, InterruptedException {

		Process proc = Runtime.getRuntime().exec("java -jar PermissionChecker.jar " + apkPath);
		proc.waitFor();
		// Then retreive the process output
		InputStream in = proc.getInputStream();
		InputStream err = proc.getErrorStream();

		byte b[] = new byte[in.available()];
		in.read(b, 0, b.length);
		String data = new String(b);
		// System.out.println("Permissions from .jar:\n"+data);
		Gson g = new Gson();

		models.ApplicationPermissionsModel p = g.fromJson(data, models.ApplicationPermissionsModel.class);
		// System.out.println(p.declared.toString());

		byte c[] = new byte[err.available()];
		err.read(c, 0, c.length);

		return p;

	}

}