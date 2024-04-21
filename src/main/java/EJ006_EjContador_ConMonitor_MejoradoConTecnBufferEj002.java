/**
 * Basicamente es idem al ej anterior del contador con monitor pero mejorandolo con la imple comun de tecn de trrabajo 
 * local acum yl solo invoc xa acum al mon metodo inc acum xa el global  
 * 
 * LO HICE PERO AL FINAL no es buen diseño xq deja la puerta abierta a q si lo hagan y fallen las muestras q quieran 
   obtener parcialmente x indet de ejec no atomica de ambos met inc get en el run ( como ya lo he explicado al ppio tmb)
	( x eso es q mejor el ej sin mons (con cerrojos , el ej002) q funca joya , y si es con mons ent hay otras formas de 
	lograr esto del mostrar parciales pero ya es con otras tecnicas q hacen al monitor hibrido de otra manera rara y q 
	son efectivas aunq no son puristas ( y x q haria eso y no con cerrojos y listo? solo si quiero q toda la logica esté 
	centralizada en lugar de repartida e/ los hilos ( osea simplifica el codigo) pero no siempre convienen. xa casos 
	simples vienen joya xq simplifican al toque el code pero sino x ahi no conviene monitores (existen otras formas)
 */
public class EJ006_EjContador_ConMonitor_MejoradoConTecnBufferEj002 implements Runnable{

	private static int contGlobal=0; // "acumulador global de trabajo"

	private static Monitor monitor=new Monitor(); 
	
	private static Object cerrojo = new Object(); 
	
	private int contInstancia=0; // "acumulador de trabajo de instancia (1 <> xa/c/hilo)"

	
	@Override
	public void run() {
		for (int i = 0; i < 200000; i++) {	 
			contInstancia++;				
		}							

		monitor.acumJob(contInstancia);
		/*synchronized (cerrojo) {
			contGlobal+=contInstancia;
		}*/
	}

	public static void main(String[] args) {

		long startTime = System.nanoTime();
		
		Thread[] arrHilos = new Thread[nroProcesadores()]; 
				
		for (int i = 0; i < arrHilos.length; i++) {
			arrHilos[i] = new Thread( new EJ006_EjContador_ConMonitor_MejoradoConTecnBufferEj002() ); 
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

class Monitor {	// SABER: EL PROBLEMA ES Q XA Q EL MON SEA PURO DEJé EL GET COMO SYNC TMB PERO AHI SI LO INVOCAN DE X MEDIO 
							// NO VAN A OBTENER EL VAL ACTUALIZADO (LO EXPLICADO EN EJ005) , Y SI EL GET NO LO HAGO SYNC 
							//	YA NO ES UN MONITOR Y NO HAY EXCLU MUTUA E/ EL ACUM Y EL GET (OSEA PUEDEN ACCEDER A LA VEZ 
							//"iNATOMICAMENTE" INCONSISTENTEMENTE. OSEA SI NO HAY EXLU MUTUA ENT HAY GRANDES POSIBILIDADES 
							//DE Q SE DE INDETERMINISMO EN ALGUN MOMENTO
	private int contGlobal = 0;
	
	public synchronized void acumJob(int acumContLocalParam) {
		contGlobal+=acumContLocalParam;
	}


	//lo hago sync total nadie "deberia" invocarlo hasta luego que todos los hilos de ejec hayan finalizado.. 
	public synchronized  int getContGlobal() {//public int getContGlobal() {//synchronized void acumJob(int acumContLocalParam) {
		return contGlobal;
	}
	
	
}
