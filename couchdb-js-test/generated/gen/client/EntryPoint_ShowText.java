package gen.client;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.os.client.ShowText;
public class EntryPoint_ShowText implements EntryPoint {
  public void onModuleLoad() {
     GWT.create(ShowText.class);
  }
}
