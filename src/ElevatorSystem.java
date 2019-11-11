import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class ElevatorSystem {
    /*******************************************************************
     * main
     *
     * Purpose: Read input & text elevator system
     *
     ******************************************************************/
    public static void main(String[] args) {
        System.out.println( "Enter the input file name (.txt files only): " );
        try{
            BufferedReader bf = new BufferedReader(new FileReader(new Scanner(System.in).nextLine()));
            String[] tokens = bf.readLine().split(" ");
            new Elevator(bf,Integer.parseInt(tokens[0]),Integer.parseInt(tokens[1]));
            bf.close();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        System.out.println("\nProcessing ends normally.");
    }
}

/*******************************************************************
 * Node
 *
 * Purpose: for the Queue class to use.
 *
 ******************************************************************/
class Node{
    private Employee e;
    private Node next;

    public Node(Employee e, Node link) {
        this.e = e;
        this.next = link;
    }
    public Node getNext()
    {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Employee getEmployee()
    {
        return e;
    }
}

/*******************************************************************
 * Queue
 *
 * Purpose: The queue must be implemented using a circular linked
 * list as described in class. The implementation must not have a dummy node and must have a single
 * pointer (called end) that points to the last node in the queue. It must be a proper queue with the
 * standard queue operations (correctly named).
 *
 ******************************************************************/
class Queue{
    private Node end;

    public Queue()
    {
        end = null;
    }

    public void enter(Employee e) {
        if(end==null) {
            end = new Node(e,null);
            end.setNext(end);
        }else {
            end.setNext(new Node(e,end.getNext()));
            end = end.getNext();
        }
    }

    public Employee leave() {
        if(end!=null) {
            if (end.getNext() == end) {
                Employee e = end.getEmployee();
                end = null;
                return e;
            } else {
                Employee e = end.getNext().getEmployee();
                end.setNext(end.getNext().getNext());
                return e;
            }
        }else {
            return null;
        }
    }

    public Employee front() {
        if(end!=null)
            return end.getNext().getEmployee();
        else
            return null;
    }

    public boolean isEmpty() {
        if(end==null)
            return true;
        else
            return false;
    }
}

/*******************************************************************
 * Stack
 *
 * Purpose: The stack must be implemented using the array imple-
 * mentation described in class. It must be a proper stack with the standard stack operations (correctly
 * named). It should not have any non-standard operations.
 *
 ******************************************************************/
class Stack{
    private int max;
    private int top; // index of the top item (if any)
    private Employee[] stackArray;

    public Stack(int max){
        this.max = max;
        stackArray = new Employee[max];
        top = -1;
    }

    public void push(Employee employee){
        top++;
        if(top<max){
            stackArray[top] = employee;
        }
    }

    public Employee pop(){
        if(!isEmpty()) {
            top--;
            return stackArray[top + 1];
        }
        return null;
    }

    public Employee top(){
        return stackArray[top];
    }

    public boolean isEmpty(){
        return top == -1;
    }
}

/*******************************************************************
 * Employee
 *
 * Purpose: An instance must contain at least the employee's ID number, what time the employee arrived at
 * the elevator queues (simulation time, not real time), what
 * oor the employee started waiting for the
 * elevator on, and what
 * oor the employee wants to go to. See also the description of the input le below
 * for more information.
 *
 ******************************************************************/
class Employee{
    private int startTime;
    private int number;
    private int arrivalFloor;
    private int destFloor;

    public Employee(int startTime, int number, int arrivalFloor, int destFloor){
        this.startTime = startTime;
        this.number = number;
        this.arrivalFloor = arrivalFloor;
        this.destFloor = destFloor;
    }

    public int getArrivalFloor() {
        return arrivalFloor;
    }

    public int getNumber() {
        return number;
    }

    public int getDestFloor() {
        return destFloor;
    }

    public int getStartTime() {
        return startTime;
    }

    public String toString() {
        return "Employee: " + number + ", arrival floor: " + arrivalFloor + ", arrival time: " + startTime+ " desired floor: " + destFloor;
    }
}

/*******************************************************************
 * Elevator
 *
 * Purpose: an instance of this class represents the entire elevator system, including the
 * elevator itself and the queues of waiting employees on each of the
 * oors the elevator serves. Furthermore,
 * you will need an array of elevator buttons that model the
 * oor buttons inside the elevator that an
 * employee presses to indicate which
 * oor they want to go to.
 *
 ******************************************************************/
class Elevator{
    BufferedReader bf;
    int elevatorCap;
    int floors;
    Stack elevator;
    Stack temp;
    Queue[] waitingUp;
    Queue[] waitingDown;
    int[] buttons;
    int time;
    boolean up;
    int currF;
    int currE;

    // stat variables
    int trips;
    double total;
    double min;
    double max;
    Employee minE;
    Employee maxE;

    public Elevator(BufferedReader bf, int elevatorCap, int floors){
        this.bf = bf;
        this.elevatorCap = elevatorCap;
        this.floors = floors;
        elevator = new Stack(elevatorCap);
        temp = new Stack(elevatorCap);
        waitingUp = new Queue[floors];
        waitingDown = new Queue[floors];
        for(int i=0; i<waitingUp.length; i++)
            waitingUp[i] = new Queue();
        for(int i=0; i<waitingDown.length; i++)
            waitingDown[i] = new Queue();
        buttons = new int[floors];
        time = 0;
        up = true;
        currF = 0;
        currE = 0;

        // stat variables
        trips = 0;
        total = 0;
        min = Integer.MAX_VALUE;
        max = Integer.MIN_VALUE;
        runElevator();
    }

    /*******************************************************************
     * arriveSingleEmployee
     *
     * Purpose: A method to process an employee arrival.
     *
     ******************************************************************/
    public void arriveSingleEmployee(String employee){
        String[] tokens = employee.split(" ");
        int number = Integer.parseInt(tokens[1]);
        int eStartTime = Integer.parseInt(tokens[0]);
        int arrivalFloor = Integer.parseInt(tokens[2]);
        int destFloor = Integer.parseInt(tokens[3]);
        Employee e = new Employee(eStartTime, number, arrivalFloor, destFloor);
        if(e.getArrivalFloor()<e.getDestFloor()) {
            waitingUp[arrivalFloor].enter(e);
            System.out.println("Time " + time + ": A person begins waiting to go up: " + employee);
        }else {
            waitingDown[arrivalFloor].enter(e);
            System.out.println("Time " + time + ": A person begins waiting to go down: " + employee);
        }
    }

    /*******************************************************************
     * arriveSingleEmployee
     *
     * Purpose: A method to model the elevator behaviour during one time unit
     *
     ******************************************************************/
    public void runElevator() {
        try{
            String employee  = bf.readLine();
            while(true) {
                // read employee from input file
                int arrivalTime = -1;
                String[] tokens;
                if(employee!=null) {
                    tokens = employee.split(" ");
                    arrivalTime = Integer.parseInt(tokens[0]);
                }
                if(time == arrivalTime) {
                    do {
                        tokens = employee.split(" ");
                        arrivalTime = Integer.parseInt(tokens[0]);
                        arriveSingleEmployee(employee);
                        trips++;
                        employee = bf.readLine();
                        if(employee!=null) {
                            tokens = employee.split(" ");
                            arrivalTime = Integer.parseInt(tokens[0]);
                        }
                    }while(arrivalTime==time&&employee!=null);
                }

                changeDirection();

                // if we did not open the elevator, go to next floor
                if(!getOut()&&!getIn()){
                    updateFloor();
                }

                if(end())
                    break;

                time ++;
            }
        }catch (IOException ioe){
            ioe.getMessage();
        }

        evaluation();
    }

    /*******************************************************************
     * end
     *
     * Purpose: Check whether there is employee to use the elevator
     *
     ******************************************************************/
    private boolean end(){
        if(!elevator.isEmpty())
            return false;
        for(int i=0; i<waitingUp.length;i++)
            if(!waitingUp[i].isEmpty())
                return false;
        for(int i=0; i<waitingDown.length;i++)
            if(!waitingDown[i].isEmpty())
                return false;
            return true;
    }

    /*******************************************************************
     * updateFloor
     *
     * Purpose: if the door did not open, would go up or down according
     * to current direction
     *
     ******************************************************************/
    private void updateFloor(){
        if(up){
            currF++;
            System.out.println("Time " + time + ": Elevator moves up to floor " + currF);
        }else{
            currF--;
            System.out.println("Time " + time + ": Elevator moves down to floor " + currF);
        }
    }

    /*******************************************************************
     * getOut
     *
     * Purpose: let the employee who arrived dest floor get out the elevator
     * & update stat variables
     *
     ******************************************************************/
    private boolean getOut(){
        if(buttons[currF]!=0){
            Stack temp = new Stack(10);
            while(!elevator.isEmpty()){
                Employee curr = elevator.pop();
                if(curr.getDestFloor()!=currF)
                    temp.push(curr);
                else {
                    System.out.println("Time " + time + ": Got off the elevator: " + curr);
                    int currTime = time - curr.getStartTime();
                    total+=currTime;
                    if(currTime<min){
                        min = currTime;
                        minE = curr;
                    }
                    if(currTime>max) {
                        max = currTime;
                        maxE = curr;
                    }
                    currE--;
                }
            }
            while (!temp.isEmpty())
                elevator.push(temp.pop());
            buttons[currF] = 0;
            return true;
        }
        return false;
    }

    /*******************************************************************
     * getIn
     *
     * Purpose: let the employee in if anybody is waiting on the current
     * floor to go in the current direction and there is still some room
     * on the elevator for another person
     *
     ******************************************************************/
    private boolean getIn(){
        if(up && !waitingUp[currF].isEmpty()&&currE<elevatorCap){
            while (!waitingUp[currF].isEmpty()&&currE<elevatorCap){
                Employee curr = waitingUp[currF].leave();
                buttons[curr.getDestFloor()]++;
                elevator.push(curr);
                currE++;
                System.out.println("Time " + time + ": Got on the elevator: " + curr);
            }
            return true;
        }else if(!up && !waitingDown[currF].isEmpty()&&currE<elevatorCap){
            while (!waitingDown[currF].isEmpty()&&currE<elevatorCap){
                Employee curr = waitingDown[currF].leave();
                buttons[curr.getDestFloor()]++;
                elevator.push(curr);
                currE++;
                System.out.println("Time " + time + ": Got on the elevator: " + curr);
            }
            return true;
        }

        return false;
    }

    /*******************************************************************
     * changeDirection
     *
     * Purpose: it decides whether to change directions or not.
     *
     ******************************************************************/
    private void changeDirection() {
        // check top & bottom
        if(up && currF == buttons.length-1) {
            up = false;
            System.out.println("Time " + time + ": Elevator changed direction: Now going down.");
        }else if(!up && currF == 0) {
            up = true;
            System.out.println("Time " + time + ": Elevator changed direction: Now going up.");
        }

        // check when elevator is empty
        if(elevator.isEmpty()){
            boolean change = true;
            if(up){
                for(int i = currF; i<buttons.length; i++){
                    if(!waitingUp[i].isEmpty()) {
                        change = false;
                    }
                }
            } else if(!up){
                for(int i = currF; i>0; i--){
                    if(!waitingDown[i].isEmpty())
                        change = false;
                }
            }

            // change direction
            if(change) {
                up = !up;
            }
            if(change && up)
                System.out.println("Time " + time + ": Elevator changed direction: Now going up.");
            else if(change)
                System.out.println("Time " + time + ": Elevator changed direction: Now going down.");
        }
    }

    /*******************************************************************
     * evaluation
     *
     * Purpose: A method to print the statistics about the simulation.
     *
     ******************************************************************/
    public void evaluation(){
        System.out.println("\nElevator simulation statistics: ");
        System.out.println("\tTotal number of trips: " + trips);
        System.out.println("\tTotal passenger time: " + total);
        System.out.println("\tAverage trip time: " + total/trips);
        System.out.println("\tMinimum trip time: "+ min);
        System.out.println("\tMinimum trip details: " + minE);
        System.out.println("\tMaximum trip time: "+ max);
        System.out.println("\tMaximum trip details: " + maxE);
    }

}