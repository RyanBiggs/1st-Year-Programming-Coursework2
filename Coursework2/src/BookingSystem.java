import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class BookingSystem {

    	// these will never change (be re-assigned)
	public final static String SEATS = "M:/Coursework2/data/seats.txt";
	public final static String WAITING = "M:/Coursework2/data/waiting.txt";
	
	public static void main(String[] args) throws Exception {
		

		final Scanner S = new Scanner(System.in);

		// returns data out of the files
		List<Seat> seats = ReturnSeats();
		List<Customer> waiting = ReturnWaitingList();
		// this one will change
		String choice = "";

		do {
			System.out.println("-- MAIN MENU --");
			System.out.println("1 : Reserve a Seat");
			System.out.println("2 : View Waiting List");
			System.out.println("3 : Cancel a reservation");
			System.out.println("4 : View reservation");
			System.out.println("Q : Quit");
			System.out.print("Pick : ");

			// saves us testing upper & lower case
			choice = S.next().toUpperCase();

			switch (choice) {
			case "1": {
				List<Seat> temp = seats;

				//does a quick filter to get rid of already reserved seats
				temp = temp.stream().filter(t -> t.getReservedBy() == null)
						.collect(Collectors.toList());
				
				// question time, each method after question filters for seats only which meet that requirement

				System.out.print("First class or Standard?: ");
				final String seatClass = S.next();
				temp = containsClass(temp, seatClass);

				System.out.print("Window or Aisle?: ");
				final String seatType = S.next();
				temp = containsSeatType(temp, seatType);

				System.out.print("With or without table?: ");
				final String hasTable = S.next();
				temp = containsTable(temp, hasTable);

				System.out.print("Forward or backwards facing?: ");
				final String facing = S.next();
				temp = containsFacing(temp, facing);

				System.out.print("Do you require an ease of access seat?: ");
				final String easeOfAccess = S.next();
				temp = containsAccess(temp, easeOfAccess);
				//do we have any seats that match?
				
				if (temp.size() > 0) {
					System.out.print(temp.size() + " Seats Available, email?: ");
					final String email = S.next().toUpperCase();
					// Get first seat available
					
					Seat firstSeat = temp.get(0);
					seats.remove(firstSeat);
					firstSeat.isReservedBy = email;
					seats.add(firstSeat);
				} else {
					System.out
					.print("No Seats Available, Waiting List? Yes/No: ");
					switch (S.next().toUpperCase()) {
					case "YES": {
						System.out.print("Enter Email: ");
						final String email = S.next().toUpperCase();
						Customer c = new Customer(email, seatClass, seatType,
								hasTable, facing, easeOfAccess);
						waiting.add(c);
					}
					case "NO": {
								
						break;
					}

					}
					
				}

			}
			break;

			case "3": {
				System.out.println("Enter email:");
				final String reserved = S.next().toUpperCase();
				Seat seat = isReservedBy(seats, reserved);
				if(seat != null)
				{
				System.out.println(reserved + " Has reserved seat number:"
						+ seat.seatNo);
				seats.remove(seat);
				// pass the seat to the next available person
				Customer c = WaitingListEntry(seat, waiting);
				if (c != null) {
					seat.isReservedBy = c.email;
				}
				// remove from waiting list
				waiting.remove(c);
				}
				else
				{
				    System.out.println(reserved + " Has no reserved seats!!");
				}
				break;
			}

			case "2": {
				System.out.println("Waiting List:");
				//loop through list spitting out the contents
				for (Customer temp : waiting) {
					System.out
					.println(String
							.format("Email: %s, Class: %s, Type: %s, Has a Table?: %s, Seat Faces: %s, Require ease of access?: %s",
									temp.email, temp.seatClass,
									temp.seatType, temp.hasTable,
									temp.facing, temp.easeOfAccess));
				}
				break;
			}
			case "4": {
				System.out.println("View seat reservation");
				System.out.println("Enter email:");
				final String reserved = S.next().toUpperCase();
				//do you have a reserved seat?
				Seat seat = isReservedBy(seats, reserved);
				if (seat != null) {
					System.out.println(reserved + " Has reserved seat number:"
							+ seat.GetSeatNo());
				} else {
					System.out.println("They have no reservations");
				}
				break;
			}

			}

		} while (!choice.equals("Q"));

		// Save stuff here
		PrintWriter outFile = new PrintWriter(SEATS);
		for (Seat s : seats) {
			outFile.println(String.format("%s,%s,%s,%s,%s,%s,%s", s.seatNo,
					s.seatClass, s.seatType, s.hasTable, s.facing,
					s.easeOfAccess, s.isReservedBy));
		}
		outFile.close();

		PrintWriter outFilew = new PrintWriter(WAITING);
		for (Customer s : waiting) {
			outFilew.println(String.format("%s,%s,%s,%s,%s,%s", s.seatClass,
					s.seatType, s.hasTable, s.facing, s.easeOfAccess, s.email));
		}
		
		// always advisable to close objects
		S.close();
		outFilew.close();
		System.out.println("Goodbye!");
	}

	public static class Seat {
		public int seatNo;
		public String seatClass;
		public String seatType;
		public String hasTable;
		public String facing;
		public String easeOfAccess;
		public String isReservedBy;

		public Seat(int startNo, String startClass, String startType,
				String startTable, String startFacing, String startAccess) {
			seatNo = startNo;
			seatClass = startClass;
			seatType = startType;
			hasTable = startTable;
			facing = startFacing;
			easeOfAccess = startAccess;
		}

		public Seat() {

		}

		public String GetAccess() {
			return easeOfAccess;
		}

		public String GetTable() {
			return hasTable;
		}

		public String GetType() {
			return seatType;
		}

		public String GetClass() {
			return seatClass;
		}

		public String GetFacing() {
			return facing;
		}

		public int GetSeatNo() {
			return seatNo;
		}

		public String getReservedBy() {
			return isReservedBy;

		}
	}

	public static class Customer {


		public String email;
		public String seatClass;
		public String seatType;
		public String hasTable;
		public String facing;
		public String easeOfAccess;

		public Customer() {
		}

		public Customer(String startEmail, String startClass, String startType,
				String startTable, String startFacing, String startAccess) {
			seatClass = startClass;
			seatType = startType;
			hasTable = startTable;
			facing = startFacing;
			easeOfAccess = startAccess;
			email = startEmail;

		}

		public String GetAccess() {
			return easeOfAccess;
		}

		public String GetTable() {
			return hasTable;
		}

		public String GetType() {
			return seatType;
		}

		public String GetClass() {
			return seatClass;
		}

		public String GetFacing() {
			return facing;
		}
	}

	public static Seat containsSeatNo(List<Seat> s, int seatNumber) {
		List<Seat> temp = new ArrayList<Seat>();
		for (Seat o : s) {
			int no = o.GetSeatNo();
			if (o == null || no != seatNumber) {
				temp.add(o);
			}
		}
		Seat seat = temp.get(0);
		return seat;
	}

	public static Seat isReservedBy(List<Seat> s, String email) {
		List<Seat> temp = new ArrayList<Seat>();
		for (Seat o : s) {
			String no = o.getReservedBy();
			//there is a weird bug that spits out nulls so need to check for that
			if (no != null && no.equals(email))
				temp.add(o);
		}
		if (temp.size() > 0) {
			Seat seat = temp.get(0);
			return seat;
		}
		return null;
	}

	public static List<Seat> containsClass(List<Seat> s, String seatClass) {
		List<Seat> temp = new ArrayList<Seat>();
		for (Seat o : s) {
			String face = o.GetClass();
			if (o == null || face.equals(seatClass)) {
				temp.add(o);
			}
		}
		return temp;
	}

	public static List<Seat> containsSeatType(List<Seat> s, String seatType) {
		List<Seat> temp = new ArrayList<Seat>();
		for (Seat o : s) {
			String face = o.GetType();
			if (o == null || face.equals(seatType)) {
				temp.add(o);
			}
		}
		return temp;
	}

	public static List<Seat> containsTable(List<Seat> s, String hasTable) {
		List<Seat> temp = new ArrayList<Seat>();
		for (Seat o : s) {
			String face = o.GetTable();
			if (o == null || face.equals(hasTable)) {
				temp.add(o);
			}
		}
		return temp;
	}

	public static List<Seat> containsFacing(List<Seat> s, String facing) {
		List<Seat> temp = new ArrayList<Seat>();
		for (Seat o : s) {
			String face = o.GetFacing();
			if (o == null || face.equals(facing)) {
				temp.add(o);
			}
		}
		return temp;
	}

	public static List<Seat> containsAccess(List<Seat> s, String easeOfAccess) {
		List<Seat> temp = new ArrayList<Seat>();
		for (Seat o : s) {
			String face = o.GetAccess().toUpperCase();
			if (o == null || face.equals(easeOfAccess.toUpperCase())) {
				temp.add(o);
			}
		}
		return temp;
	}

	public static List<Customer> containsCustomerClass(List<Customer> s,
			String seatClass) {
		List<Customer> temp = new ArrayList<Customer>();
		for (Customer o : s) {
			String face = o.GetClass();
			if (o == null || face.equals(seatClass)) {
				temp.add(o);
			}
		}
		return temp;
	}

	public static List<Customer> containsCustomerSeatType(List<Customer> s,
			String seatType) {
		List<Customer> temp = new ArrayList<Customer>();
		for (Customer o : s) {
			String face = o.GetType();
			if (o == null || face.equals(seatType)) {
				temp.add(o);
			}
		}
		return temp;
	}

	public static List<Customer> containsCustomerTable(List<Customer> s,
			String hasTable) {
		List<Customer> temp = new ArrayList<Customer>();
		for (Customer o : s) {
			String face = o.GetTable();
			if (o == null || face.equals(hasTable)) {
				temp.add(o);
			}
		}
		return temp;
	}

	public static List<Customer> containsCustomerFacing(List<Customer> s,
			String facing) {
		List<Customer> temp = new ArrayList<Customer>();
		for (Customer o : s) {
			String face = o.GetFacing();
			if (o == null || face.equals(facing)) {
				temp.add(o);
			}
		}
		return temp;
	}

	public static List<Customer> containsCustomerAccess(List<Customer> s,
			String easeOfAccess) {
		List<Customer> temp = new ArrayList<Customer>();
		for (Customer o : s) {
			String face = o.GetAccess().toUpperCase();
			if (o == null || face.equals(easeOfAccess.toUpperCase())) {
				temp.add(o);
			}
		}
		return temp;
	}

	public static Customer WaitingListEntry(Seat seat,
			List<Customer> customersList) {
		List<Customer> temp = customersList;
		temp = containsCustomerClass(temp, seat.GetClass());
		temp = containsCustomerSeatType(temp, seat.GetType());
		temp = containsCustomerTable(temp, seat.GetTable());
		temp = containsCustomerFacing(temp, seat.GetFacing());
		temp = containsCustomerAccess(temp, seat.GetAccess());
		if (temp.size() > 0) {
			return temp.get(0);
		}
		return null;
	}

	public static List<Seat> ReturnSeats() 
			throws FileNotFoundException {
		Scanner input = new Scanner(new FileReader(SEATS));
		String[] str;
		List<Seat> seats = new ArrayList<Seat>();
		while (input.hasNext()) {
			// next line trims the new line so re-include

			String test = input.nextLine();
			if (test != null || test != "") {
				str = test.split(",");
				Seat temp = new Seat();
				// map values
				temp.seatNo = Integer.parseInt(str[0]);
				temp.easeOfAccess = str[5].trim();
				temp.facing = str[4].trim();
				temp.hasTable = str[3].trim();
				temp.seatClass = str[1].trim();
				temp.seatType = str[2].trim();
				temp.isReservedBy = str[6].trim();
				seats.add(temp);
			}
		}
		input.close();

		for (Seat s : seats) {
			if (s == null) {
				seats.remove(s);
			}
		}
		return seats;
	}

	public static List<Customer> ReturnWaitingList()
			throws FileNotFoundException {
		Scanner input = new Scanner(new FileReader(WAITING));
		String[] str;
		List<Customer> seats = new ArrayList<Customer>();
		while (input.hasNext()) {
			// next line trims the new line so re-include
			String test = input.nextLine();
			if (test != null || test != "") {
				str = test.split(",");
				Customer temp = new Customer();
				// map values
				temp.seatClass = str[1].trim();
				temp.seatType = str[2].trim();
				temp.hasTable = str[3].trim();
				temp.facing = str[4].trim();
				temp.easeOfAccess = str[5].trim();
				temp.email = str[0].trim();
				seats.add(temp);
			}
		}
		input.close();
		return seats;
	}
}
