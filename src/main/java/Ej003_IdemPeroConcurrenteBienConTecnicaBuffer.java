
public class Ej003_IdemPeroConcurrenteBienConTecnicaBuffer implements Runnable {

	/** PARA ACUMULAR TRABAJO DE <>S HILOS EN UN RECURSO GLOBAL STATIC: SIEMPRE QUE NECESITE HACER ESO, SIEMPRE DEBO EMPLEAR 
	 * 	LA TECNICA DE BUFFER : Que consiste simplemente en q cada hilo trabaje sobre una copia (var de instancia )q acumule 
	 *  su trabajo sin necesitar cerrojo de nada (del rec compart) ni bloquearlo ni nada ,y recien luego q termina su trabajo 
	 *  acumulado en su var local ahi si mete una sentencia sync(cerrojoStatic) xa acum el contGlobal+=contInstancia y listo! 
	 * */
	
	private static int contGlobal=0; // "acumulador global de trabajo"

	private static Object cerrojo = new Object(); 
	
	private int contInstancia=0; // "acumulador de trabajo de instancia (1 <> xa/c/hilo)"

	
	@Override
	public void run() {
		for (int i = 0; i < 200000; i++) {	 
			contInstancia++;				
		}							

		synchronized (cerrojo) {
			contGlobal+=contInstancia;
		}
	}

	public static void main(String[] args) {

		long startTime = System.nanoTime();
		
		Thread[] arrHilos = new Thread[nroProcesadores()]; 
				
		for (int i = 0; i < arrHilos.length; i++) {
			arrHilos[i] = new Thread( new Ej003_IdemPeroConcurrenteBienConTecnicaBuffer() ); 
			arrHilos[i].start();
		}		
		
		try {
			for (int i = 0; i < arrHilos.length; i++) {
				arrHilos[i].join();
			}
			
		} catch (Exception e) {
		}
		
		System.out.println("contGlobal ( trabajo acumulado) --> " + contGlobal);
		
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
