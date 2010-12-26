package gen.client;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.os.client.MapTest;
public class EntryPoint_MapTest implements EntryPoint {
  public void onModuleLoad() {
     GWT.create(MapTest.class);
  }
}
