package heron.gameboardeditor.datamodel;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import com.google.gson.*;

public class ProjectIO {
	
	public static void save(Grid grid, File output) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		FileWriter writer = new FileWriter(output);
		gson.toJson(grid, writer);
		writer.close();
	}
	
	public static Grid load(File input) throws JsonSyntaxException, JsonIOException, IOException {
		Gson gson = new Gson();
		FileReader reader = new FileReader(input);
		Grid grid = gson.fromJson(reader, Grid.class);
		reader.close();
		return grid;
	}

}
