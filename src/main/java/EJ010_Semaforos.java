import java.util.concurrent.Semaphore;

/**
 * Es un mecanismo provisto x el api xa ctrlar facil la concurrencia pidendo pasar yl pasa otro yl otro 
 * pero q ademas de permitir asi de a 1, permite n , osea tam del sem = 3 quiere decir q pueden acceder 3
 * a la vez ( todo imple auto x api , noi a mano como en los ej002 003 etc !! ) tmb podria impl el api de sems pero ya existe!
 * Ej si tengo 3 ventanillas xa atender a 100 personas, ent hay 3 recursos dispo a la vez (//) xa 100 clis , hilos, procesos etc
 * ent con usar el api de sems y .aquire y .release auto el api de sems controla todo
 * 
 *   https://www.youtube.com/watch?v=yD8JjGQsDP8  !!!!!!!!!!
 *   
 *   SABER LA POSTA: --> Hasta ahora con la seccion critica indicabamos que un solo hilo podia acceder a la vez a ese codigo
 *   	( a la vez es exacto // , solo 1 puede a la seccion critica ) ;Peero CON EL API DE SEMAFOROS podemos indicar cuantos 
 *   	hilos (clis) pueden entrar a la seccion critica (usar el rec compartido) a la vez ( y auto controla q no haya 
 *   	problemas de concurr!! )
 *   
 *      Ejemplo:  Si tengo 3 ventanilla y 100 hilos cli , antes con synchronized con lock o con mon solo el code critico 
 *      				o metodo critico o lo q fuera podia ser accedido por un unico hilo a la vez! Osea, otorgaba 
 *      				la > concurrencia posible pero xa las partes criticas solo permitia 1 a la vez. (eso en todos 
 *      				los ejs que hice h' ahora , osea si eran 10 hilos y queria mas recursos hilos cli ent el pool 
 *      				me daba esos hilos cli , con eso mejoraba performance de cant hilos reuso etc costo creacion etc!
 *      				peeero c/hilo aunq corrieran 8 a la vez la reg critica sync locked solo podia ser accedida x 1 a la vez
 *      
 *        			bueno si yo quiero q mas accedan , ej de las ventanillas, hasta 3 hilos a la vez accedan al rec compartido 
 *        			ventanillas ( a <> vent pero al mismo rec compart) ent debo usar este mec q ya lo implementa auto xa mi.
 *        			( lo mismo es si quisiera q 3 a la vez pudieran acceder a la reg critica o metodo sync y sumaran 3 a la vez,
 *        				me daria inconsistencia xa lo q son el caso de las sumas cont++ x ej o acums PEEERO xa los casos q son
 *        				como este ejemplo de n peluqueros, n ventanillas bco , osea n recs, y a la vez hasta 3 x ej o x , ent 
 *        				lo implemento siempre con semaforos de ese tam y listo el api de semaphore de java me lo resuelve todo
 *        				solo debo usar ese api q se usa con sus metodos .acquire y realease() basicamente  
 *         			
 *        	
 */

// Se implementa asi:
public class EJ010_Semaforos implements Runnable{ 

	private final Semaphore semaforoLocal;
	private final int idHilo;
	
	public EJ010_Semaforos(Semaphore semaforoParam, int idHilo) {
		this.semaforoLocal = semaforoParam;
		this.idHilo = idHilo;
	}

	@Override
	public void run() {

		System.out.println("Hilo (o cliente) " + idHilo + " está esperando en la fila");
		try {
			semaforoLocal.acquire();	// --> SABER! --> sem.acquire hace 3 cosas --> 1) avisa q va a tomar el turno (avisa a los demas hilos que va a entrar a la seccion critica ( o a la ventanilla ) )
																	//			   --> 2) y el hilo se detiene ahi hasta q haya un semaforo disponible en el semaforo y ahi realmente tome el turno
			// --> 3) toma el turno y ejec sus sigs lineas de code post al aquire
			System.out.println("Hilo (o cliente) " + idHilo + " está ocupando(usando) la ventanilla"); 
			
			//PROCESO: hace sus cosas ( vamos a emular el procesamiento de sus cosas con un retardo de 2 segs ) 
			Thread.sleep(2000);
			//--fin proceso
			
			System.out.println("Hilo (o cliente) " + idHilo + " está saliendo (liberando) la ventanilla");
			
			semaforoLocal.release();
			
		} catch (InterruptedException e) { 						
			
			e.printStackTrace();
		}
	}
	
	//Hilo ppal de ejec de la app / punto de entrada ejecutable main 
	public static void main(String[] args) {
		
		Semaphore semaforoDe3InstanciaMain = new Semaphore(3); // 3 como ej de q comparto hasta 3 recs (ventanillas) en // real!

		//creo 5 hilos (clis) , pasandoles a todos el mismo semaforo q he creado como usuario mainapp q los lanza y el idHilo 
		for (int i = 0; i < 5; i++) {
			Thread t = new Thread( new EJ010_Semaforos(semaforoDe3InstanciaMain,i) );
			t.start();
		}
		
		// y listooooooooooooooo ( todo lo demas concurr //ismo exclu mut 3 a la vez, no mas esperas todo lo maneja el api sem
		
		// Ej de ejecucion. salida:
		//		Hilo (o cliente) 0 está esperando en la fila			//Obs:  1ero los 5 esperan y se muestra esperando 
		//		Hilo (o cliente) 1 está esperando en la fila			//		xq el syso esta antes del aquire; luego del
		//		Hilo (o cliente) 4 está esperando en la fila			//		aquire ocupoando hasta 3 al toque pero luego 
		//		Hilo (o cliente) 3 está esperando en la fila			//		se intercalan saliendo con ocupando h' fin.
		//		Hilo (o cliente) 2 está esperando en la fila			//		( todo auto , todo como qria 3 recs compart 
		//		Hilo (o cliente) 1 está ocupando(usando) la ventanilla  //      y en // ! y sin problemas de concurencia!)
		//		Hilo (o cliente) 4 está ocupando(usando) la ventanilla
		//		Hilo (o cliente) 0 está ocupando(usando) la ventanilla			//saber: siempre antes de la toma del sem 
		//		Hilo (o cliente) 1 está saliendo (liberando) la ventanilla		// ejecuta antes el esperando de los 5 
		//		Hilo (o cliente) 4 está saliendo (liberando) la ventanilla		// xq esa inst es re al toque xq tengo 8 cpus
		//		Hilo (o cliente) 0 está saliendo (liberando) la ventanilla		// (8>5) ent se ejec siempre eso q es rapido
		//		Hilo (o cliente) 3 está ocupando(usando) la ventanilla			//	antes del api del aquire completo 
		//		Hilo (o cliente) 2 está ocupando(usando) la ventanilla			//  de alguno. luego si 3 aquiere q es el max
		//		Hilo (o cliente) 3 está saliendo (liberando) la ventanilla		// yl si se intercalan los liberando con usando
		//		Hilo (o cliente) 2 está saliendo (liberando) la ventanilla		// mientras se liberan usan los q qdan. (todo auto)

	}
}
