
public class Ej002_CondGuarda_Wait_NotifyAll implements Runnable{

	/**
	 * En este ejemplo voy a poner yo el orden en que quiero que se ejecute cada hilo
	 * ( para esto voy a usar la funcion wait ( obvio junto con el bloque synchronized sobre un cerrojo static ) )
	 * 
	 * Como siempre el orden de ejec de c/hilo es "aleatorio" x el SO asi que dentro de c/u q el S.O. ejecuta verificn 
	 * ellos ( x/c/ej de S.O.  a c/u ) si les toca el turno (la cond q les puse y sino se duermen h q en alguna se va a 
	 * cumplir ent ahi ejecutan su codigo synchronized (thread safe mutual exclusion x synchronized y terminan su eje y ya 
	 * no se ejecutan mas xq salen de todo el bloque sync y se despiertan los demas hilos en cola del bloque sync 
	 * (obs 1: si huvbieran mas lineas de ejec en el thread pero fuera del bloque sync se seguirian ejecutando en // esa es 
	 * la gracia, lo mutuamente excluido es solo la region critica del code (el cont++ ( y en este caso el ctrl de 
	 * prioridades x orden y su cond de guarda junto con los wait y notifyall xa implementar este orden al codigo critico 
	 * cont++( osea q al bloque sync le sumo todas esas verificaciones cond guarda wait y notifyall ) 
	 * ( obs 2: el while está dentro del bloque sync xq usa el rec compart cont en su validacion )
	 * 
	 * CLAVE: PRE-SABER --> CUANDO UN HILO SE DESPIERTA DE LA COLA DEL CERROJO ( LA COLA DEL CERROJO ES LA DEL WAIT) ( Y 
	 * SE DESPIERTA POR UN NOTIFYL/ALL EXPLICITO (SOBRE EL MISMO CERROJO OBVIO)) ENTONCES: CUANDO UN HILO SE DESPIERTA DEL 
	 * CERROJO(COLA DEL WAIT) --> PASA A LA COLA DEL SUNCHRONIZED (DEL BLOQUE )!! SON 2 COLAS <>S la del sync y la del wait!!
	 * 
	 * mnemo: LA COLA DEL CERROJO O WAIT ES COMO UNA COLA MAS PROFUNDA O SUBCOLA DE LA COLA DEL BLOQUE SYNCHRONIZED !!

	 * SUPER EXPLICACION POSTA DE COMO FUNCIONA EN DETALLE TODO BY CHARLY SAN 2024: ( <-- CLAVE: Saber esto explica todo !! ) 
	 * ___________________________________________________________________________
	 * 
	 * ( cuando un bloque sync termina despierta a cond de concurso (o carrea de ejec) a los hilos en cola de ese bloque 
	 *   synchronized. eso es auto cuando un hilo termina un bloque sync. se despiertan auto los demas en cola del bloque 
	 *   ;pero si estaban dormidos con wait (xq se ejecutan , en cualquier orden, pero el wait estaba xa ctrlar de mandarse 
	 *   a dormir a la cola del cerrojo o de wait digamos , esto quiere decir que entraran otros hilos a ejecutarse xq este 
	 *   no terminó su syncronized pero se durmió asi q la cola del bloque si se activa x el wait xa darle a otro , estos 
	 *   podran accesar ent (entrar) al bloque sync (el q gane la carrera) y verificará tmb si es su turno, sino se duerme y 
	 *   lo mismo entrará otro a ejec el bloque sync , ahora supongamos q si entró q el q le tocaba el turno x su nro de id 
	 *   con respecto a cont actual, ent ese va a ejecutar el bloque completo ( no solo el if o mas bien while sino q ahi ya 
	 *   entra y hará el cont++ y mostarse , y ahi va a terminar su ejec y morrir x q termina no solo su bloque sync sino todo 
	 *   su codigo ( y si hubiera mas code y no es critico tmpoco ya nos interesa mas, q haga lo q tnga q hacer total ya 
	 *   no jode) PEROO terminar su bloque sync solo activa la cola del bloque sync PERO NO DESPIERTA A LOS Q ESTABAN 
	 *   EXPLICITAMENTE DORMIDOS CON WAIT XQ SE HABIAN EJECUTADO Y NO ERA SU TURNO, POR LO QUE ESOS DEBEN SER DESPERTADOS SI 
	 *   O SI JUSTO ANTES DE TERMINAR LA EJEC DEL BLOQUE SYNC XA EL Q SI SE PUDO EJECUTAR. ESO VA A HACER Q VUELVAN A LA COLA 
	 *   DEL SYNC! SINO NUNCA IBAN A VOLVER! XQ DORMIAN MAS PROFUNDAMENTE EN LA COLA DEL WAIT ( Y EL WAIT RECORDEMOS Q ESTABA 
	 *   XQ SINO EL THREAD MORIA OSEA SI NO CUMPLE LA COND AL EJECUTARSE NO PUEDE NI TERMINAR NI ENTRAR EN EL BUCLE SIN 
	 *   AUTODORMIRSE SINO  LOOP INFINITO ENT LA FORMA JUSTAMENTE ES AUTODORMIRSE CON WAIT PERO NO ES LA COLA DEL DEL BLOQUE 
	 *   SYNC XQ SINO REENTRARIAN A PROBARSE MIL VECES LOS MISMOS Q NO LES TOCABA POR ESO MEJOR NO VOLVERLOS A LA COLA DEL 
	 *   SYNC SINO DEJARLOS FUERA (DORMIDOS MAS PROFUNDO EN LA DEL WAIT OSEA EN OTRO LADO Y LIBERAR CON EL MISMO WAIT LA COLA 
	 *   DEL BLOQUE SYNC XA Q ENTRE OTRO PERO Q YA NO SEA EL , OSEA SE AUTO EXCLUYE, SE AUTOEXLUYEN LOS HILOS Q NO LES TOCA 
	 *   DE LA COLA DEL BLOQUE SYNC ASI OBVIO VAN A NO REINTENTARSE AL PEDO CUANDO YA SABEMOS Q DEBE ENTRAR OTRO Y NO EL. 
   
	 *       
	 */
	
	private static int cont=0; 
	
	private static Object cerrojo = new Object();  

	private int id; //id xa c/hilo
	
	public Ej002_CondGuarda_Wait_NotifyAll(int id) {
		this.id = id;
	} 
	
	@Override
	public void run() {
		//code threads..
		
		//System.out.println("soy el hilo: " +id);
		//synchronized (cerrojo) {
			
			//version mal: si entra h3 1ero y cont <> 0 sale del sync y muere no va a dormir , x eso el wait y la cond debe 
			//	ser ciclica sino no vuelve a la linea de la cond osea al if sino q retoma desde donde fué a dormir y la linea 
			//  sig deberia ser de nuevo verificar y no terminar por eso la cond es idem pero en un while (cond ciclica o 
			//  cond "de guarda" ) 
			
			/*if ( id == cont ) {
				System.out.println("Soy el hilo:" + id);
				cont++;
			} */
			
			// version bien:
		
			// bien implementada:
			
			//while ( cont < 8 ) { //cant hilos
				//if (id != cont) {//if (id == cont) {
			synchronized (cerrojo) {
				
			
				while (id != cont) {//if (id == cont) {
					try {
						cerrojo.wait();//wait(); SABER: ( el wait lo manda a la cola del cerrojo si no es su turno..)
														//wait se despierta solo x notify y desde donde se durmió! ( x eso la condicion ciclica)
					} catch (Exception e) {			// (siempre habrá q despertarlos explicitamente! (notifyAll())
						e.printStackTrace();
					}
				}
				System.out.println("Soy el hilo:" + id);	//SABER CLABE: AL SALIRSE UN HILO DEL SYNCHRONIZED SOLO SE DESPIERTAN LOS HILOS 
															//Q ESTABAN EN LA COLA DEL BLOQUE SYNCHRONIZED PERO NO LOS QUE ESTABAN EN LA 
				cont++;										//COLA DE WAIT ( SON 2 COLAS , LA DEL SYNC Y LA DE WAIT )
				cerrojo.notifyAll(); //notifyAll(); // SABER: notyfyall solo despierta a los hilos q estan durmiendo x el wait 
			}
			//} 

			/** mal implmentada
			while ( cont < 8 ) { //cant hilos
				if (id == cont) {
					System.out.println("Soy el hilo:" + id);
					cont++;
					notifyAll();
				}else {
					try {
						cerrojo.wait();//wait();	
					} catch (Exception e) {
						e.printStackTrace();//System.out.println("wait error on thread: " + id);
					}
					
				}
			} */
				
		//}
	}

	public static void main(String[] args) {
		
		Thread[] arrHilos = new Thread[nroProcesadores()]; 
		
		//creo y lanzo los hilos:
		
		for (int i = 0; i < arrHilos.length; i++) {
			arrHilos[i] = new Thread( new Ej002_CondGuarda_Wait_NotifyAll(i)); 
			arrHilos[i].start();
		}		
		
		// joineo el thread main a todos los hilos xa q espere a q todos terminen yl recien seguir su cod restante (syso(cont))
		try {
			for (int i = 0; i < arrHilos.length; i++) {
				arrHilos[i].join();
			}
			
		} catch (Exception e) {
		}
		
		System.out.println("soy el hilo main post threads joineados");
	
	}
	
	public static int nroProcesadores() { 
		Runtime runtime = Runtime.getRuntime();
		return runtime.availableProcessors();
	}

}
