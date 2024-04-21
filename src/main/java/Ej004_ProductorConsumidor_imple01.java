
/**
 * Resumen de la solución:
 *  
 *  Consumidor: si hay tarta le resto 1 a la variable tarta ( q es un cont de porciones mas q nada ) 
 *  														(osea resto 1 al rec compart de n recursitos finitos x vez) 
 *  Consumidor: si no hay tarta (ni una porcion) ent Despierto al cocinero + me duermo como consumidor (a la espera)
 *  								 ( osea los clientes despiertan al prod y pasan a dormir xa que no sigan pidiendo )
 *  Cocinero:   me duermo esperando a que me llamen  
 *  	( osea el cocinero siempre va a estar durmiendo hasta que lo llamen, se ejecute, produce 10 y se autoduerme de nuevo)
 *  Cocinero:	si me llaman produzco 10 trozos de tarta y me duermo
 *  ( osea en este ej le pone un 10 a la variable de tarta ( la var tarta es en si un cont porcs restantes de la tarta ) ) 
 */

public class Ej004_ProductorConsumidor_imple01 implements Runnable{

	private boolean consumidor; // a fines practicos no creo 2 clases <>s sino q uso la misma c/1flag q diferencie el caso
	
	private static int tarta = 0;	// recurso compartido consumible modificable 
	
	private static Object lock = new Object();	// creo el cerrojo static = xa todas las inst de hilos ( / lock / candado )
	
	static final int PORCIONES_X_TARTA = 10;
	
	static final int PRODCUTORES = 1;
	static final int CONSUMIDORES = 10;
	static final int PARTICIPANTES = PRODCUTORES + CONSUMIDORES;

	private int id; 
	
	public Ej004_ProductorConsumidor_imple01(boolean isConsumer, int id) {
		this.consumidor = isConsumer;
		this.id = id;
	}
	
	@Override
	public void run() {
		while (true) {		// SABER: CLAVE: --> el bucle infinito es xa q los consumidores SIEMPRE esten consumiendo y 
							//														el productor siempre esté produciendo. 
			if ( consumidor) {
				
				consumiendo();
				
			}else { // (productor )
				cocinando();
			}
		}
		
	}
	
	private void consumiendo() {
		synchronized (lock) {
			if ( tarta > 0 ) {		// -> si hay tarta ( Y RECORDEMOS Q VENGO DE LOS IF DE ARRIBA OSEA Y QUE SOY CONSUMIDOR!)
				tarta --; 			// -> 	consumo  --> ( modifico al recurso compartido en su cantidad ! )
				System.out.println("soy el consumidor " + id + " y quedan:  " + tarta + " porciones de tarta");
				try {
					Thread.sleep(1000);// --> SABER --> Thread.sleep DUERME AL PROGRAMA ENTERO !!! (lo hago xa realentizar)
				} catch (InterruptedException e) {
					e.printStackTrace();
				}   
			} else { // tarta == 0 ( NO quedan tartas (porciones) Y SOY CONSUMIDOR )
				lock.notifyAll();  //  -> despierto al productor!!!! --> SABER (NEW) -> no puedo despertar a 1 solo q yo quiera,
										// siempre tiene que ser notifyAll , asi q se despertarán todos luego del notifyall TODOS
										// (eso implica todos los 11 hilos, los 10 consumidores( alguno/s tmb estaria/n durmiendo,
									    //  pero el productor segur o se despierta entre ellos y ent ya le va a tocar el turno y aun 
										//  con mas seguridad le va a tocar xq c/vez q no hay tarta el consumidor ademas de despertar 
										//	a todos los q dormian, el mismo se va a ir a dormir con wait x lo q se autoexcluye xa x lo
										//	menos las posibilidades xa q se ejecute el productor aumenta.
				try {
					lock.wait();  // a dormir a la subcola ( cola de wait ) ( solo despertables x notifyAll )
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 											// ANALISIS BY CHARLY SAN 2024 LA POSTA: 
															// ____________________________________
			}						// !! LA CLAVE -> si despierta a TODOS incluso a el aunq el no necesita xq estaba en ejec,
									//					y LUEGO SE VA A DORMIR a la cola del wait ( ent él ya no queda 
									//	"ready xa ejec" en la cola del synchronized ( solo hasta q otro lo despierte ) 
									//	obvio q si entra x ej otro consumidor ent lo va a despertar a este en su ejec de notifyall x q 
									//	no habia mas tartas y en su afan de despertar al cocinero ent en este ej la autoexclusion es en 
									//	el peor de los casos de si mismo pero no asegura q otro consumidor pidiendo sin q haya tartas 
									//	aun lo reactive xa volver este tal vez sin tartas a ejec y asi osea q siempre va a 
									//	haber 1 posibilidad entre 10 (9 cons y 1 prod) de ejec el prod ( al menos no es 1 
									//	entre 11) osea siempre se autoexluye 1 en este ej ( por ahi podria mejorarlo con una 
									//	variable booleana compartida xa solo notifyall si ningun hilo consumidor previo ya ha invocado 
									//	el notyfy all , si es asi ok el code este de notyfyall y sino solo me duermo (ahi ya aumento 
									//	las posibilidades q se ejecute mas rapido, al toque , el productor, ya q todos se autoexluirian
									//	( osea estaria condicionando el notifyall de los consumidores de cuando quieren despertar al 
									//	productor SIEMPRE ( cuando siempre talvez podria controlarse como digo) pero esta prueba la 
									//	dejo para otra version del programa. por ahora esta funciona aunq las probabilidades sean de 
									//	1/10 siempre igual de tantas vueltas el productor va a terminar ejecutandose ( y no siempre en 
									//	priori 10 sino q ese seria el peor caso ) igual si todo fuera mas pesado los procesos //s y 
									//	producciones y consumisiones ent si deberia mejorar esto de las probabilidades xa q se 
									//	despierte mas rapido al productor.
									//					____________________________________________________________
		}
	}

	private void cocinando() {
		synchronized(lock) {
			if ( tarta == 0 ) {
				tarta = 10;		  // -> produzco ( modifico al recurso compartido en su cantidad ! )
				System.out.println("soy el productor(cocinero) y quedan:  "+ tarta +" porciones de tarta");
				lock.notifyAll(); // -> despierto a todos los q dormian. 
			}
			try {
				lock.wait();	  // --> produje o no me voy a dormir siempre como productor  
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

	public static void main(String[] args) {
		
		int numhHilos=PARTICIPANTES;
		
		Thread[] hilos = new Thread[numhHilos];
		
		//1) Creo los hilos:
		
		//a) creo el productor ( 1 solo x ahora xa este ejemplo ) 
		
		hilos[0] = new Thread ( new Ej004_ProductorConsumidor_imple01(false,0) ); // genero en la pos 0 al consumidor
		
		//b) creo los consumidores ( 10 xq van a ser 10 comenzales de tarta )
		
		for (int i = 1; i < hilos.length; i++) {
			hilos[i] = new Thread ( new Ej004_ProductorConsumidor_imple01(true,i) ); 
		}
		
		//2) Lanzo TODOS los hilos ( 1 productor y 10 consumidores ( 11 threads //s de la clase ej004 (run() method ) ) .. 
		
		for (int i = 0; i < hilos.length; i++) {
			hilos[i].start();
		}
		
		
		//3) Creo (como 100pre) el proced de join a todos xaq el main los espere antes de ejec su sig cod qes mostrar rtados
		for (int i = 0; i < hilos.length; i++) {
			try {
				hilos [i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
