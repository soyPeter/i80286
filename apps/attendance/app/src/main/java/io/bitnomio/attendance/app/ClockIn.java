package io.bitnomio.attendance.app;

import io.bitnomio.attendance.domain.actions.GetClockInData;
import io.bitnomio.attendance.domain.actions.SaveClockInData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ClockIn {

  public final static Logger LOG = LoggerFactory.getLogger(ClockIn.class);

  private final GetClockInData getClockInData;

  private final SaveClockInData saveClockInData;

  public ClockIn(GetClockInData getClockInData, SaveClockInData saveClockInData) {
    this.getClockInData = getClockInData;
    this.saveClockInData = saveClockInData;
  }

  public void perform() {

    // validate if the user is already clocked in
    //  if yes, throw exception

    var existingClockInData = getClockInData.execute(null, null);

    if (existingClockInData.isPresent()) {
      throw new IllegalStateException("User is already clocked in");
    }

    //  if not, save the clock in data
    saveClockInData.execute(null, null);

  }

}
