
/**
 *	Basicamente hace lo mismo que el ejemplo 1 del contador pero en vez de usar cerrojo usa una clase como monitor
 *	Saber -> Monitor: Es una clase con todos sus metodos public synchronized ( si 1 no lo es ent no es un monitor puro )
 *	Saber -> el synchronized va a nivel de cada metodo, si tengo inc() e incrementa el cont global 20mil veces en 1 ciclo
 *				ent de nuevo no estoy haciendolo muy // q digamos y es el rend simil a si no usara hilos (sec)
 *	Saber -> el cont estará en la clase q actua como monitor y no hace falta q sea static ya q la idea es que crearemos, 
 *			    osea usaremos, el monitor como atributo static en la clase main (q a su vez es la clase hilo (run() )
 *				ent siempre habrá 1 unica inst del moniutor y x lo tanto 1 unica inst de sus atribs aunq no sean static 
 *				x mi var del tipo monitor es creada 1 sola vez a nivel bloque static en la def del atrib q la apunta 
 *	Saber -> COMO FUNCIONA ? facil, al ser todos los metodos sync y la sync es a nivel metodo java auto genera exlusion 
 *			 mutua e/ estos. por lo q no se pueden ejecutar mas de 1 metodo activo del monitor a la vez!
 *			( x eso si tengo otro metodo getCOunt() no se va a entremezclar con el inc ) 
 *			PEEEROOO no garantiza el orden de ejecucion de c/metodo sync ( el orden es indet ) x lo q si yo tenia en mi 
 *			codigo run de cada hilo algo como inc() yl syso(getcount()) x ahi al terminar inc() se libera el monitor 
 *			(xq inc yl getcount osea mi code de run no es sync sino q usa el monitor q tiene los metodos sync el ) 
 *			ent como decia se libera hilo q ejecutó inc termina el inc yl como el mon se libera entra otro hilo <> ej el h2,
 *			pregunta: esta libre el mon y si ( xq el h1 aun no llegó a ejec x algun motivo todavia el getcount) 
 *			ent se termina ejec de nuevo el inc esta vez x el h2 pero luego cuando el h1 haga el mostrar tendrá el cont 
 *			no de esa inst q qria sino el actual con todo ya sumado tmb x el h2 . EN RESUMEN, EL RTADO SE LOGRA OK XQ 
 *			EL MONITOR PURO ASEGURA LA MUTUA EXLUSION. PERO EL ORDEN DE LAS LLAMADAS A SUS <>S METODOS ES INDEFINIDOS 
 *			Y PUEDE EJEC 5 VECES EL INC Y RECIEN EL 1ER MOSTRAR Q ME VA A DAR 100K YA XQ EL CONT SE INC 5 VECES X EJ.
 *			VENTAJA MONITORES: NO ES LO MEJOR PERO GARANTIZA DE FORMA SUPER FACIL LA MUTUA EXCLUSION (LA COHERENCIA DEL ACCESO AL RECURSO) 
 *			DESVENTAJA (INDET EN EL ORDEN DE LLAMADA A SUS <>S METODOS
 *			PERFORMANCE: ESTE EJ SENCILLO ES SEGURO PERO NO ES PERFORMANTE , PARA Q LO SEA DEBO APLICAR LA MISMA TECNICA DE 
 *			BUFFER Q UTILICé XA LA VERS DE CERROJO (EJ002) OSEA EN ESTE CASO EN EL INC NO TENER EL WHILE (2K) CONT++; 20K VECCES
 *			SINO agregar un atrib de instancia a la clase de los hilos (ej005 main class q tmb es hilo (run() ) y poner 
 *			en el run la acumulacion a la local con el while 20k (todo sin call method mon alguno, osea sin usar/ bloquear nada,
 *			solo trabaja el hilo y acumula "su trabajo " (suma 1) en su acum (cont) local y recien luego el call al metodo inc 
 *			pasandole ya un param (mi contlocal) y q este synchronized metod del mon el cual inc lo acumule al global
 *		
 *			SABER -> MONITORES (all sus pub meths sync) no usan cerrojo porque el cerrojo es la misma clase osea es auto osea 
 *			no se explicita ningun cerrojo x lo q ahi si valen los wait() y notifyAll() de una sin cerrojo.wait/notif 
 *
 *			SABER-> SI QUIERO GARANTIZAR EL ORDEN DE EJEC , COMO EL MON NO EJEC LOS METODOS EN BLOQUE Y EL ORDEN DE LAS <>S  
 *						EJEC DE SUS METHS ES INDET ENT PUEDO HACER UN ATRIB ORDEN Y EL ATRIB idDE INSTANCIA xa c/hilo y ent 
 *						solo ejecutar el inc si es q le toca el turno ( id = order ) ( donde order es un contador static q 
 *						+ bien podria llamarse hActual o leTocaAlHxxx; y va de 0 a 7 x ej (8cpus) ent si se ejc 2 inc seguidos
 *						ya no voy al menos a ejec el codigo interno de este q es el contglobal+=... sino lo pongo en a dormir 
 *						con wait   ( la cola del momnitor es de espera, la cola de wait es de dormidos (son cosas y colas<>s)
 *						ent si entra oootro inc lo mismo se duerme y si ahi entra el mismo h q está activo ent ejecuta y el 
 *						getcount xq le tocó en su cola del mon ent a su vez pasa la cond del if le toca del get y ent muestra 
 *						y ahi inc order++ despierta  todos los q dormian x las dudas y termina su ejec x su run termina 
 *						ent ya qdaran los otros 7 hilos y pasará igual ej entra el h4 al inc ent no le toca x order ent 
 *						se autoexluye a dormir al wait xa aument las posib de sig ejec del mon sea otro hilo al menos y no el 
 *						y asi x ahi entran el h5yh7 y se duermen x lo mismo hasta q en un momento entra el h2 al mon xq le toca
 *						al pedir el inc() se lo permiten ( gana la carrera/concurso del mon) y pasa el if del inc xq order=cont 
 *						osea q le toca ent incrementa el laburo acumulado y sale, luego quiere pedir el mostrar (get() del mon) 
 *						pero ya se metió otro hilo ej el 3 pero como order sigue en 2 xq solo se incrementa cuando termiana 
 *						completo el hiloactual osea luego o mas en el metodo get q es el ultimo met del mon q requiere sync q 
 *						usa el run ( si el run tuviese mas code abajo q no usa el mon ni me importa, eso es // y no afecta ya 
 *						en nada xq se supone q si no está controlado es codigo no critico (sin conflicto x + q // ) 
 *						ent como decia el h3 se duerme y asi varios pero en un momento el h2 puede ejecutar el mon y ent el 
 *						getCont() del mon y mostrar ( y asi.. y como vemos c/u aca si va a mostrar el acum hasta esa inst xq 
 *						x + q el orden de ejec de los metodos del mon es indet y posib repe mezclas etc yo con la var order 
 *						y los ifs de order ( q deben ser while (conds ciclicas x los wait) !! sino retomarian de abajo cuando 
 *						despierten y no volverian a comprobar si les toca  y ejecutarian... ) ent con eso yo "syncronizo osea
 *						ordeno en este caso secuencialmente xq asi lo quise x eso el orde y el ++ y el if cont=order ( podrian
 *				  	   	establecerse otros criterios de orden mas complejos mtb pero la idea seria la misma solo camb la cond) 
 *						ent asi pude mostrar h0 20k , h1 40k, h2 60k y asi en vez de la indet de orden de ejec ho h1 40 40 etc
 *						q llegaria a 160 xq la mutua exclu de inc funciona pero el intercalado de llamadas a los metodos no seria 
 *						como yo lo quisiera (atomicamente) x eso con la metodologia de ctrl de orden no puedo evadir el mec 
 *						intrinseco de la indet de ejec de meths sync de un mon pero si condicionar con order wait contt++ notify 
 *						etc para no ejec de nuevo el mismo metodo inc (osea se ejec si pero no pasa el if) y se manda a autodormir
 *						ent controlo ejecs con ifs ya q no puedo controlar q no se ejecuten (xq asi funcs mons order invoc meths 
 *						indet eso 100pre es asi en mons) pero lo pude controlar con esta mecanica de order (q es la misma q aplic
 *						en el ej002).   
 *						EN RESUMEN: XA COSAS SIMPLES SIMPLIFICAN EL CODIGO (SYNC AL METODO DEL MON Q SERIA EL ADM CENTRALIZADO
 *						DE LAS OPS y asi no anda distrib la logica de inc o mostrar en las clases q se ejc como hilos (run())
 *						Peeero x ej si quiero imple mejor performance q algo sec no es tan facil imple la de acum trabaj local
 *						y recien luego solo sync inc del mon acum contlocalparam a contglobal en mon ( no se xq xq xa mi 
 *						sacando el while del inc() y poniendolo en el run de c/hilo xa acum loc 20k veces yrecien luego llamado 
 *						al inc solo xa acumloc a glob funcaria pero en el vid 4.1 dice q los monitores no te permiten asi y 
 *						q se puede pero de otras formas q implican si o si crear metodos en el mon xa acum el trabajo local 
 *						y q esos no serian sync y ent el monitor no seria puro ( ya no seria un monitor x tener 1 met no sync) 
 *						q ahi funcionaria pero q no es un monitor sino un hibrido ya, lo cual en la practica esta bien aunq 
 *						no es una teoria purista y facilmente comprensible xa un equipo de trabajo ( medio raro todo esto q dice,
 *						xq carajos quiere meter la logica de acum local en el mon si es uno solo? no va en el run de/hilo?
 *						osea idem ej002 no se puede? raroo.. ( si tengo tiempo lo pruebo..) OSEA ESTA ES MI PREGUNTA A ESE VID:
 *						
 *						pregunta: al final, xa darle permormance y que el monitor no ejecute todo secuencial con el while 20k 
 *						en su metodo inc() y dice que xa impl la tecn de buffer ya vista no se podria sin hacer del mon un 
 *						hibrido , por que ? porque menciona meter la logica de acum local en el mon (en un metodo no sync en 
 *						el mon q haga eso) si el mon es uno solo porque es una var static en la main app , y porque no 
 *						simplemente como en la tecn buffer de el ej de cerrojos no va el while 20k en el run sin sync xa la 
 *						acum local a la var local contlocal  yl despues un llamado a un inc() del mon q solo acumule 
 *						cont+=contLocal (conLocal atrib de instancia, xa cada hilo )  osea todo igual q en cerrojos no se 
 *						podria sin afectar la pureza del monitor? osea no entiendo esa parte que menciona q para imple la 
 *						tecnica de buffer no se puede en este ej con mons hacerlo simil a como hicimos con cerrojos y que 
 *						habria forzadamente q hacer un hibrido ?
 *						
 *						¿¿ O TAL VEZ CON HIBRIDO SE REFIERE A Q TNDRIA PARTE DE LA LOGICA DEL TRABAJO EN EL RUN DEL HILO Y NO 
 *							SOLO LLAMADOS A UN MON CENTRALIZADO Q IMPL TODA LA LOGICA ? A ESO SE REFIERE CON HIBRIDO ??
 *  
 *						RTA: no creo me conteste pero yo tranquilamente puedo contestarme haciendolo en una vers 2 de este ej 
 *						con mon pero q intente aplicar mi idea normal de tecn buffer idem impl a ej002 con cerrojo(candado/lock)      
 *	
 *						ENTONCES: ESTE EJEMPLO LO HAGO NORMAL SIN TECNICA DE BUFFER , y le añado como extra la parte de orden
 *								( luego en otro ej006..._conTecnBuffer copio esto y le añado la tecn de buffer al ej mon )
 */

//https://www.youtube.com/watch?v=1lmNSi_AuYI&list=PLw8RQJQ8K1ySGcb3ZP66peK4Za0LKf728&index=13&pp=iAQB
public class Ej005_EjContador_ConMonitor implements Runnable{

	private static int contGlobal=0; // "acumulador global de trabajo"

	private static Monitor1 monitor=new Monitor1(); // lo puse asi xq ya existia la clase monitor def en ej006  
	
	private static Object cerrojo = new Object(); 
	
	private int contInstancia=0; // "acumulador de trabajo de instancia (1 <> xa/c/hilo)"

	
	@Override
	public void run() {
		/*for (int i = 0; i < 200000; i++) {	 
			contInstancia++;				
		}							

		monitor.acumJob(contInstancia);
		*/
		
		monitor.acumJob(); //manda a producir (sumar) 20k de una al method sync con while 20 k con cont++ del mon
		
													 //ANALISIS CHARLY SAN 2024 LA POSTA:
													 //_________________________________
		System.out.println(monitor.getContGlobal()); // intenta ejec el get luego del inc pero no necesariamente le toca 
													 // x lo q pueden entrar otros al inc negando a este get xq es 1 solo 
		/*synchronized (cerrojo) {					//	metodo a la vez en monitores x lo q como explico al ppio pueden 
			contGlobal+=contInstancia;				//  mostrarse valores desactualizados x 3o4 incs juntos x ej yl los 
													//mostrar ya con cont cambiado (updated)
													
														(tmb saber q esta ejec es segura x mutex (mutua excl) auto de mons 
														// pero no es permormante xq el while con 20k está todo en un bloque
														// apropiativo en el mon. (en el EJ006 hago esto mismo pero con 
		}*/												//	tecnica de buffer idem al ej002 y lo mejoro en rendimiento 
														//  aunq ya es un monitor raro xq implica una logica de orden de 
														//  invoc ( cierto constraint de q no "deberia" llamar al get antes 
														//  q hayan terminado todos los cont y eso no es buen diseño xq deja 
														//  la puerta abierta a q si lo hagan y fallen las muestras q quieran 
														// obtener parcialmente x indet de ejec no atomica de ambos met inc 
														// y get en el run ( como ya lo he explicado al ppio tmb) 
	}													//
														// y como lo vemos ya en una 1era ejec de ejemplo
														//	20000
														//	60000	--> getlocal retrasado, con valor conglobal mas  
														//	80000					acumulado x otros mas
														//	40000
														//	100000
														//	120000
														//	140000
														//	160000
														//	contGlobal ( trabajo acumulado) --> 160000
														//	Tiempo transcurrido: 0.0191182 segundos
													  
														// como vemos pasa eso (la indet del orden de llamadas ya q el run 
														// tiene 2 instruc no atomicas q pueden interrumpirse y asi obtener 
														//  rtados o comportamientos no deseados; PEERO el rtado final de 
														//  la acum no se ve afectado x el orden de ejxs ya q el mon al -
														//  garantiza de forma auto sencilla la mutua exclu.
														//  ( luego es poco perf simil sec ent ej006 mejora pero npo es 
														//		diseño confianble, es propenso a mal uso y errores, no obliga 
														//		un buen uso sino q deja la puerta abierta . 
														//	x lo q deberia hacer un ej007 con un hibrido pero ya fué, x ahora 
														//  dejo mons solo x 1 metodo y sencillo y centralizado o varios 
														//	pero q no importe orden y podria mejorarlo idem 006 pero q el 
														//  getcont no sea sync y q sepan q corre suelto loquito y puede dar
														//  cualquier cosa. y si requieren una solucion mas confiable x ahora 
														//  con mons no la sé xq tndria q ver lo del hibridismo xa lograrlo 
														//  pero al menos lo se hacer como en ej002 con cerrojos(lock,candado)
	public static void main(String[] args) {

		long startTime = System.nanoTime();
		
		Thread[] arrHilos = new Thread[nroProcesadores()]; 
				
		for (int i = 0; i < arrHilos.length; i++) {
			arrHilos[i] = new Thread( new Ej005_EjContador_ConMonitor() ); 
			arrHilos[i].start();
		}		
		
		try {
			for (int i = 0; i < arrHilos.length; i++) {
				arrHilos[i].join();
			}
			
		} catch (Exception e) {
		}
		
		System.out.println("contGlobal ( trabajo acumulado) --> " + monitor.getContGlobal() ); // contGlobal);
		
		//end time
		long endTime = System.nanoTime();
		
		long elapsedTimeInNano = endTime - startTime;
	    double elapsedTimeInSeconds = (double) elapsedTimeInNano / 1_000_000_000.0;

	    System.out.println("Tiempo transcurrido: " + elapsedTimeInSeconds + " segundos");

	}

	public static int nroProcesadores() {
		Runtime runtime = Runtime.getRuntime();
		return runtime.availableProcessors();
	}
}

class Monitor1 {	 
	private int contGlobal = 0;
	
	public synchronized void acumJob() {
		for (int i = 0; i < 20000; i++) {
			contGlobal++;
		}
	}

	public synchronized  int getContGlobal() {
		return contGlobal;
	}		
}
