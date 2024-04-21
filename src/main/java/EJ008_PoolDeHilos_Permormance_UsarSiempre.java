/**
 * -> EL POOL DE THREADS ES SIEMPRE MUCHO MEJOR QUE LA FORMA CLASICA MANUAL DE LANZAR THREADS <- ( usar siempre )
 *  
 * Basicamente crea un eje con una matrux (arr de 2 dims [f][c]) y la popula con nros int rnd, luego 
 * quiere q c/fila sea atendida x 1 hilo <> osea c/hilo atienda a 1 fila.
 * El problema es q si tengo 20 filas yo lanzo 20 hilos ( pero al pedo xq no tengo 20 procesadores como xa q 
 * 	los 20 se ejecuten en // sino q van en forma concurrente; Peeero en memoria hay creados 20 objs hilo con su mem adm etc!!
 * si tuviera 5000 filas tendria 5 mil hilos , una locura xq no tngo 5mil procesadores ni tanta mem xa mat de 5kfilas y 
 * 	encima 5k hilos su mem y su adm!
 * POR ESO SIEMPRE XA PROCESAR A LA VEZ N COSAS CON N > NRO PROCESADORES -> SIEMPRE CONVIENE POOL DE THREADS EJ DE 8 !!
 * Y VOY PIDIENDOLE AL POOL DE A UNO, SI NO TIENE MAS ESPERA A Q UN THREAD TERMINE Y EN VEZ DE MORIR VUELVE AL POOL Y EL POOL
 * YA LO TIENE MARCADO COMO LISTOXAUSAR/REUSAR Y ASI SIEMPRE SE USAN 8 . MI PROG LE PIDE UN HILO (DISPO)AL POOL Y LO USA 
 * (LO CORRE) LUEGO LE PIDE OTRO Y IDEM, CUANDO YA LLEGO A PEDIRLE X EJ 8 Y LE PIDO UN 9 SIN LIBERAR NINGUNO ENT EL POOL 
 * RECIEN CREA UNO NUEVO XA NO COLGARME Y PASA A TAM + 1 (9) PERO CRECE MENOS Q TENER 50MIL XQ OTROS SE VAN LIBERANDO Y 
 * EL POOL EN VEZ DE CREAR ME VA DANDO LOS  Q SE LIBERARON (MARCADOS COMO READYTOUSE pone  ) algo asi...
 * 
 * https://www.youtube.com/watch?v=rzh2Cj40HA8&list=PLw8RQJQ8K1ySGcb3ZP66peK4Za0LKf728&index=14&pp=iAQB   
 * 
 * SABER: LA POSTA: ENB REALIDAD LO Q PUSE AL PPIO ERA LO Q YO ME IMAGINABA PERO EL VID EXPLICA Q LA POSTA EN REALIDAD ES 
 * 			Q EL POOL TIENE UNA COLA DE TAREAS (de clis, peticiones de hilo) Y SI NO TIENE DISPO LO PONE EN LA COLA HASTA
 * 			Q UNO SE LIBERE ( mi prog es inconsciente de eso, tan solo tardará un poquito mas mi cli xq no habia hilos dispo 
 * 			xa reusar, ya sean hilos o recursos xq un pool puede ser en si de cualq tipo de recurso, el tema es q yo no quiero 
 * 			q el pool crezca, quiero limitar ese nro max de recs ent x eso la cola de tasks ( mientras q mi 1er pensamiento
 * 			fue arriba el de "no voy a dejar esperando x el recurso.. creo otro crezco dyn pero safo ) pero si no quiero 
 * 			crecer mas ent es asi con la cola como explica el vid
 * 
 * 			Ademas concluyo q ni siquiera deberian crearse x ej en un pool fixed (fijo) de 8 recs los 8 recs de una 
 * 			xq xahi se usan 2o3 y al pedo creé los 8! osea qse van creando a medida q se necesitan y haya <= tam pool creados
 * 			ent ahi si creo pero si mejor los ya creados estan libres ent no creo sino q reuso,
 * 			y si x otro lado debo atender pero no hay mas lugar xa crear ni ninguno ready xa reusar de los 8 del pool ent 
 * 			a la cola de tasks del pool y cuando uno se libera ahi lo otorga. 
 * 
 *  		( esa es la posta como lo hace el vid, ahora veamos como se implementa todo esto (vá**) 
 */

// (viene**)
// Implementacion Pool de threads ( vers fixed pool ) 

public class EJ008_PoolDeHilos_Permormance_UsarSiempre {

	// https://www.youtube.com/watch?v=rzh2Cj40HA8&list=PLw8RQJQ8K1ySGcb3ZP66peK4Za0LKf728&index=14
}
