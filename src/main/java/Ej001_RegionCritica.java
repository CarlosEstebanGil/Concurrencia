import java.util.Iterator;

/**	( OJO , ESTE NO IMPLEMENTA LA ACUMULACION EN FORMA REALMENTE CONCURRENTE YA QUE SYNCHRONIZA TODO EL BLOQUE SOBRE EL 
 * 		CERROJO DEL ACUM GLOBAL (static cont ) x lo q impide a los demas hasta q el no termina todo su bucle, y si cambio y 
 * 		pongo el synchronized solo al cont++ ent el overhead de miles de intercambios tan solo x la unica linea cont++ de
 * 		cada vuelta de cada hilo hace q tarde todo muchisimo mas ( como mil veces mas ) osea empeora la performance y el de 
 * 		todo el bloque sync sobre el cont global tiene la misma performance q si ni hubiera usado hilos osea mejor q el overhead tremendo del sync solo al cont dentro del bucle pero no mejor sino igua o un poco peor q si directamente lo hubiera hecho sin hilos. para acum trabajo sobre una misma cosa a modificar q es global quiero evitar cerrojo de todo un bloque , yo puedo hacer un trabo mis 20mil veces o lo q sea pero lo hago sobre una var local de MI-TRABAJO q es acumulable luego recien en una sent sync al final del hilo pero todo lo demas se hizo sobre una var local ent solo uso el cerrojo osea la var global xa acumularle el trabajo q ya hice loca. siempre se debe hacer asi, esta tecnica se llama tecnica de buffer y es xa esto, acumular trabajo de cada hilo a un acum cont static global comun a todos . 
 * 		y siempre poner casos de prueba con muchos cpus y muchas iteraciones , seguir c/hoja casos prueba xa seguir c/var y 
 * 		c/cola ( en este caso hay una, si hubiera mas cerrojos o sentencias wait notify habria mas colas q seguir ) y siempre 
 * 		medir el rendimiento con <>s alternativas xa saber si mejora o empeora!!!!! .  
 * 1 main
 * n inst runnableprincipal threads
 * 
 * Problematica: n hilos (c/n>1) acceden a 1 mismo recurso compartido (static cont) 
 * 				 		y lo modifican (inc en 1 , 20milveces en/c/paso.
 * 
 * Indetermincacion: si no se controla, se pisan y no va a dar los 20mil x 8 = 160.000 pretendidos, 
 * 	incluso siempre va a dar <> (indet). recordemos que con++; es cont = cont + 1 ; inst no necesariamente atomica xa el S.O. 
 * 
 * Solucion: proveer un mecanismo para que 2 o mas hilos en // escriban de forma simultanea en la misma variable 
 *  
 */
public class Ej001_RegionCritica implements Runnable{

	private static int cont=0; //recurso compartido xa modif x <>s threads ( entonces : controlar indeterminacion )
	
	private static Object cerrojo = new Object(); // debe ser static! (sino c/hilo tendria su propia vers de cerrojo y no sirve) 
									   // (es un cerrojo xa todos) y puede ser de cualquier clase, solo se utiliza como cerrojo.
	@Override	
	public void run() {  //codigo de cada hilo:
		
		synchronized (cerrojo) { // NEW! -> SABER -> si ponia el synchronized solo en cont++ tardaba muchismo mas! segura-
								 //					 mente x el overhead de miles de intercambios de veces de hilos c/suma!
								 //					 (SE PONE A TODO PERO ES COMO SI FUERA SECUENCIAL! NO GANO PERFORMANCE!)
								 // ENT SABER-> Xa inc cont en // debo usar tecnica de buffer osea q c/u tenga su propio 
								// 	cont y ahi si q no bloquean ni el while ni se intercambian mil veces por sentencia cont++ 
								//  sino q lo hacen todo sin bloqear nada osea sin pedir cerrojo de nada xq manejan su propio 
								//  cont interno cada inst de hilo y solo cuando termina el while y sumo todo su contador ahi 
								//  mete un bloque sync solo xa acumularlo al global ( pero mientras labura libre solo 
								//  totalmente en // xq trabaja sobre acumular lo suyo en su copia sin joder a nadie, luego su 
								//  laburo acumulado recien al final en 1 sola linea syn lo acumula a lo q tenga el cont 
								//	global !!
								// LEARNING ABSOLUTO: ESTA VERS ES IDEM SEC Y ACUM DEBE HACERSE CON TECNICA BUFFER MENCIONADA
								//						(ver Ej003_IdemPeroConcurrenteBienConTecnicaBuffer)
								//	  Y SIEEEMPREEE MEDIR LOS TIEMPOS !!!!!!!!!!!!!				
			for (int i = 0; i < 200000; i++) { //20k ciclos de insts x c/thread. (20k xq cuanto > es el nro de inst x hilo ent
										  //		mejor se prueba que salte indeterminacion seguro si la hay.
			
			//cont++;	// todos van a incr (modif) la var compartida cont
			//synchronized(cerrojo) {  // (**) activos: h1,h4, cola: h4, h3, y los va switcheando de activo a la cola y a carrera de ejecucion x cada vez q este bloque o linea de codigo es ejecutada ( eso hace q esta linea se ejecute x 1 solo a la vez ) 
									 // SOLUCION! una var obj cerrojo compartido xa todas de cualquier tipo  
				cont++;				//				+ un bloque synchronized en la region critica (regionconflictiva)
			//}						//			Este mecanismo garantiza la exlusion mutua ( el proposito de solucion q era q  
		}							//			multiples hilos no puedan modificar un obj o var (recurso) compart a la vez)
	  }
	}
	
	// RTADO: COORECTO (160MIL) SIN IDETERMINACIONES. SE GARANTIZA LA EXLUSION MUTUA. 
	// 		  SE CONTROLA CON SYNCHRONIZED LA SECCION CRITICA DE CODIGO CONVIRTIENDOLA EN UNA 
	//        	REGION CRITICA (SECCION CRITICA CONTROLADA). 
	
	// EXPLICACION PERFECTA BY CHARLY SAN 2024:
	
	// COMO FUNCIONA EN DETALLE INTERNAMENTE EL SYNCHRONIZED : ver ** ( seguimiento de ej de activos y cola )
	//     Basicamente funciona asi: ej entra el h1 1ero, entra x 1era vez al for (q tiene una cond i <20000) si cumple entra
	//			y tiene al toque un bloque sync xa la sent cont++; ent pregunta al api java, este blooque (este cerrojo) 
	//				esta en uso ? . como no ent lo ejecuta. ni bien sale (se termina) del bloque sync se libera el cerrojo y se activa la carrera 
	//				de ejec xa los q estuvieran en cola del cerrojo, como aca aun no habia nada no pasa nada. ahora supongamos 
	//				q pega otra vuelta el h1 , ok todo igual pasa x el for x la 2da vuelta (i vale 2) preg x el cerrojo 
	//				(peeero en ese momento en paralelo habia entrado h4 x ej y habia entrado a su bucle y habia entrado al 
	//				cerrojo y aun está laburando con eso ( modificando el cont) ent se le niega al h1 ( mas bien se lo pone 
	//				en la cola del cerrojo xa q espere xq hay otro haciendo la modif) ent el h4 completa atomicamente el 
	//				cont = cont +1 y todo lo q tenga en su bloque de modif de recs compart / seccion critica y cuando lo 
	//				libera ( ya actualizado pero bien no a media instruccion ni nada, bien atomico y todo su bloque critico , 
	//				aca es solo 1 linea de cod  pero podrian ser mas cosas ) garantizado ese bloque atomicamente de 1 a la vez,
	//				es liberado osea termina el bloque (el cont++) y se activa la carrera de ejec xa los q estaban en la cola que 
	//				era el h1 en su 2da vuelta de ejec y ahi si puedo operar ( pero ya con el valor bien actualizado  completo x
	//				el h2) y tmb lo va a hacer completo xq ahora el cerrojo lo tiene el (h1) y asi!! genial!!  
	
	public static void main(String[] args) {

		// init time
		long startTime = System.nanoTime();
		
		/*Principal principal = new Principal();
		
		Thread t1 = new Thread(principal);
		t1.start();*/
		
		// Thread[] hilos = new Thread[2]; //h0,h1.
		Thread[] arrHilos = new Thread[nroProcesadores()]; 
		
		//Runnable runnablePrincipal = new Principal();
		for (int i = 0; i < arrHilos.length; i++) {
			arrHilos[i] = new Thread( new Ej001_RegionCritica() ); // (runnablePrincipal)
			arrHilos[i].start();
		}		
		try {
			//t1.join();
			
			/*for (Thread threadn : hilos) {
				threadn.join();  } */
			for (int i = 0; i < arrHilos.length; i++) {
				arrHilos[i].join();
			}
			
		} catch (Exception e) {
		}
		
		System.out.println(cont);
		
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
