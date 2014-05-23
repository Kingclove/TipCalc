package com.scheme.tipcalc;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public class TipCalc extends Activity {
	
	// Constants used when saving and restoring
	
	private static final String TOTAL_BILL = "TOTAL_BILL";
	private static final String CURRENT_TIP = "CURRENT_TIP";
	private static final String BILL_WITHOUT_TIP = "BILL_WITHOUT_TIP";
	
	private double billBeforeTip; // Users bill before tip
	private double tipAmount; // Tip amount
	private double finalBill; // Bill plus Tip
	
	EditText billBeforeTipET;
	EditText tipAmountET;
	EditText finalBillET;
	
	private int[] checkListValues = new int[12];
	
	CheckBox friendly_check;
	CheckBox specials_check;
	CheckBox opinion_check;
	
	RadioGroup availableRadioGroup;
	RadioButton bad_radio;
	RadioButton ok_radio;
	RadioButton good_radio;
	
	Spinner problem_solving;
	
	TextView time_for_service;
	Chronometer timeWaiting;
	long secondsYouWaited = 0;

	
	Button start_button;
	Button pause_button;
	Button reset_button;
	
	
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tip_calc); // Inflate the GUI
		
		// Check if app just started, or if it is being restored
		
		if(savedInstanceState == null){
			
			// Just started
			
			billBeforeTip = 0.0;
			tipAmount = .15; 
			finalBill = 0.0; 
			
		} else {
			
			// App is being restored
			
			billBeforeTip = savedInstanceState.getDouble(BILL_WITHOUT_TIP);
			tipAmount = savedInstanceState.getDouble(CURRENT_TIP); 
			finalBill = savedInstanceState.getDouble(TOTAL_BILL); 
			
		}
		
		// Initialize the EditTexts
		
		billBeforeTipET = (EditText) findViewById(R.id.billEditText); // Users bill before tip
		tipAmountET = (EditText) findViewById(R.id.tipEditText); // Tip amount
		finalBillET = (EditText) findViewById(R.id.finalBillEditText); // Bill plus tip
		
		// SECOND PART ---------------
		
		// Initialize the SeekBar and add a ChangeListener
		
		tipSeekBar = (SeekBar) findViewById(R.id.changeTipSeekBar);
		
		tipSeekBar.setOnSeekBarChangeListener(tipSeekBarListener);
		
		// ---------------------------
		
		// Add change listener for when the bill before tip is changed
		
		billBeforeTipET.addTextChangedListener(billBeforeTipListener);
		
		
		friendly_check = (CheckBox) findViewById(R.id.friendlyCheckBox);
		specials_check= (CheckBox) findViewById(R.id.specialsCheckBox);
		opinion_check= (CheckBox) findViewById(R.id.opinionCheckBox);
		
		setUpIntroCheckBoxes();
		
		
		availableRadioGroup = (RadioGroup) findViewById(R.id.availableRadioGroup);
		bad_radio = (RadioButton) findViewById(R.id.badRadio);
		ok_radio= (RadioButton) findViewById(R.id.okRadio);
		good_radio= (RadioButton) findViewById(R.id.goodRadio);
		
		addChangeListenersToRadios();
		
		problem_solving = (Spinner) findViewById(R.id.problemSolvingSpinner);
		addItemSelectedListenerToSpinner();
		
		timeWaiting = (Chronometer) findViewById(R.id.timeWaitingChronometer);
		time_for_service = (TextView) findViewById(R.id.ChromometerTextView);
		
		
		start_button = (Button) findViewById(R.id.startButton);
		pause_button= (Button) findViewById(R.id.pauseButton);
		reset_button= (Button) findViewById(R.id.resetButton);
		
		setButtonClickListener();
		
	}
	
	
	
	



	private void addItemSelectedListenerToSpinner() {
	    
	    problem_solving.setOnItemSelectedListener(new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
			int position, long id) {
		   
		    	checkListValues[6] = (problem_solving.getSelectedItem()).equals("Bad")?-1:0;
	    		checkListValues[7] = (problem_solving.getSelectedItem()).equals("OK")?3:0;
	    		checkListValues[8] = (problem_solving.getSelectedItem()).equals("Good")?6:0;
		    
	    		setTipFormWaitressChecklist();
			updateTipAndFinalBill();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		    // TODO Auto-generated method stub
		    
		}
	    });
	    
	}



	private void addChangeListenersToRadios() {
	    availableRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	        
	        @Override
	        public void onCheckedChanged(RadioGroup group, int checkedId) {
	    		checkListValues[3] = (bad_radio.isChecked())?-1:0;
	    		checkListValues[4] = (ok_radio.isChecked())?2:0;
	    		checkListValues[5] = (good_radio.isChecked())?4:0;
	    		
	    		
	    		 setTipFormWaitressChecklist();
			  updateTipAndFinalBill();
	        }
	    });
	    
	}



	private void setUpIntroCheckBoxes() {
	    
	    friendly_check.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
		   checkListValues[0] = (friendly_check.isChecked())?4:0;
		   
		   setTipFormWaitressChecklist();
		   updateTipAndFinalBill();
		}
		
	    });
	    
	    
	    specials_check.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
		   checkListValues[1] = (specials_check.isChecked())?1:0;
		   
		   setTipFormWaitressChecklist();
		    updateTipAndFinalBill();
		}
		
	    });
	    
	    opinion_check.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
		   checkListValues[3] = (opinion_check.isChecked())?2:0;
		   
		   setTipFormWaitressChecklist();
		    updateTipAndFinalBill();
		}
		
	    });
	}
	
	private void setTipFormWaitressChecklist() {
	    int  checkListTotal = 0;
	    
	    for(int item:checkListValues){
		checkListTotal += item;
	    }
	    
	    tipAmountET.setText(String.format("%.02f", checkListTotal*0.01));
	    
	}

	// Called when the bill before tip amount is changed
	private TextWatcher billBeforeTipListener = new TextWatcher(){

		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			
			try{
				
				// Change the billBeforeTip to the new input
				
				billBeforeTip = Double.parseDouble(arg0.toString());
				
			}
			
			catch(NumberFormatException e){
				
				billBeforeTip = 0.0;
				
			}
			
			updateTipAndFinalBill();
			
		}
		
	};
	
	// Update the tip amount and add tip to bill to
	// find the final bill amount
	
	private void updateTipAndFinalBill(){
		
		// Get tip amount
		
		 tipAmount = Double.parseDouble(tipAmountET.getText().toString());
		
		// The bill before tip amount was set in billBeforeTipListener
		
		// Get the bill plus the tip
		
		 finalBill = billBeforeTip + (billBeforeTip * tipAmount);
		
		// Set the total bill amount including the tip
		// Convert into a 2 decimal place String
		
		finalBillET.setText(String.format("%.02f", finalBill));
		
	}
	
	// Called when a device changes in some way. For example,
	// when a keyboard is popped out, or when the device is 
	// rotated. Used to save state information that you'd like
	// to be made available.
	
	protected void onSaveInstanceState(Bundle outState){
		
		super.onSaveInstanceState(outState);
		
		outState.putDouble(TOTAL_BILL, finalBill);
		outState.putDouble(CURRENT_TIP, tipAmount);
		outState.putDouble(BILL_WITHOUT_TIP, billBeforeTip);
		
	}
	
	// ---- END OF FIRST PART ----
	
	// ---- SECOND PART ----------
	
	// SeekBar used to make a custom tip
	
	private SeekBar tipSeekBar;
	
	private OnSeekBarChangeListener tipSeekBarListener = new OnSeekBarChangeListener(){

		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			
			// Get the value set on the SeekBar
			
			tipAmount = (tipSeekBar.getProgress()) * .01;
			
			// Set tipAmountET with the value from the SeekBar
			
			tipAmountET.setText(String.format("%.02f", tipAmount));
			
			// Update all the other EditTexts
			
			updateTipAndFinalBill();
			
		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	// ---- END OF SECOND PART ----------
	
	
	private void setButtonClickListener(){
		
		start_button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				// Holds the number of milliseconds paused
				
				int stoppedMilliseconds = 0;
				
				// Get time from the chronometer
				
			    String chronoText = timeWaiting.getText().toString();
			    String array[] = chronoText.split(":");
			    if (array.length == 2) {
			    	
			    	// Find the seconds
			    	
			      stoppedMilliseconds = Integer.parseInt(array[0]) * 60 * 1000
			            + Integer.parseInt(array[1]) * 1000;
			    } else if (array.length == 3) {
			    	
			    	// Find the minutes
			    	
			      stoppedMilliseconds = Integer.parseInt(array[0]) * 60 * 60 * 1000 
			            + Integer.parseInt(array[1]) * 60 * 1000
			            + Integer.parseInt(array[2]) * 1000;
			    }
			    
			    // Amount of time elapsed since the start button was
			    // pressed, minus the time paused

			    timeWaiting.setBase(SystemClock.elapsedRealtime() - stoppedMilliseconds);
			    
			    // Set the number of seconds you have waited
			    // This would be set for minutes in the real world
			    // obviously. That can be found in array[2]
			    
			    secondsYouWaited = Long.parseLong(array[1]);
			    
			    updateTipBasedOnTimeWaited(secondsYouWaited);
			    
			    // Start the chronometer
			    
			    timeWaiting.start();
				
			}
			
			
		});
		
		reset_button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				timeWaiting.stop();
				
			}
			
			
		});
		
		pause_button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				timeWaiting.setBase(SystemClock.elapsedRealtime());
				
				// Reset milliseconds waited back to 0
				
				secondsYouWaited = 0;
				
			}
			
			
		});
		
	}
	
	private void updateTipBasedOnTimeWaited(long secondsYouWaited){
		
		// If you spent less then 10 seconds then add 2 to the tip
		// if you spent more then 10 seconds subtract 2
		
		checkListValues[9] = (secondsYouWaited > 10)?-2:2;
		
		// Calculate tip using the waitress checklist options
		
		setTipFormWaitressChecklist();
				
		// Update all the other EditTexts
				
		updateTipAndFinalBill();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tip_calc, menu);
		return true;
	}

}
