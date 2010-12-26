package gen.client;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.os.client.ListTest;
public class EntryPoint_ListTest implements EntryPoint {
  public void onModuleLoad() {
     GWT.create(ListTest.class);
  }
}
