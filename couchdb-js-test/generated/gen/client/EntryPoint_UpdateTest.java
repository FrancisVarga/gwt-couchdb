package gen.client;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.os.client.UpdateTest;
public class EntryPoint_UpdateTest implements EntryPoint {
  public void onModuleLoad() {
     GWT.create(UpdateTest.class);
  }
}
