package hadouken;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import io.reactivex.schedulers.Schedulers;
import org.junit.*;
import org.junit.runner.RunWith;

@RunWith(HierarchicalContextRunner.class)
public class ObservableSqsTest {
  private List<SimpleMessage> _simpleMessageList = new ArrayList<>();
  private ObservableSqs _observableSqs;

  @Before
  public void before() throws Exception {
    _observableSqs = new ObservableSqs(new SingleMessageClientFacade());

    _observableSqs.readMessages()
      .observeOn(Schedulers.io())
      .subscribeOn(Schedulers.computation())
      .subscribe(_simpleMessageList::add);

    // NOTE(justin.morgan): There must be a better way to do this.
    Thread.sleep(500); //give the observable time to populate the list
  }

  @Test
  public void itShouldReadOneMessage() throws Exception {
    final int expectedSize = 1;

    assertEquals(expectedSize, _simpleMessageList.size());
  }
}
