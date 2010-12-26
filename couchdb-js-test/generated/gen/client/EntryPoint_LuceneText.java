package gen.client;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.os.client.LuceneText;
public class EntryPoint_LuceneText implements EntryPoint {
  public void onModuleLoad() {
     GWT.create(LuceneText.class);
  }
}
