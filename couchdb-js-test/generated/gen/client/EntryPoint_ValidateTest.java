package gen.client;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.os.client.ValidateTest;
public class EntryPoint_ValidateTest implements EntryPoint {
  public void onModuleLoad() {
     GWT.create(ValidateTest.class);
  }
}
