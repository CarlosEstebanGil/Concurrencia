/**
 * Basicamente es hacer un contglobal ++ en forma concurr sin q se pisen y sobreescriban vals y me dé de menos
 * ( en el ej2 lo controlabamos con cerrojos, en otros hicimos uno con monitor) BUENO OTRA OPCION ES SOLO 
 *  DECLARAR Q EL CONT ES DE TIPO ATOMICINTEGER y LISTO!! él solo se encarga de de gestionar quien puede y 
 *  acceder y quien no al hilo y si otro quiere desatomizar la op de ++ sobre ese int q wrapea el atomicinteger 
 *  no puede porque ese tiene inc() etc y BASICAMENTE LO Q ASEGURA ES Q NO SE DA ESO DE INTERRUMPIR LA INST CONT = CONT +1;
 *  POR EL SOLO SE ENCARGA DE EJC ATOMICA SIN Q NADIE LO INTERRUMPA 
 *  ( ES AUTO. YO ME ABSTRAIGO. NO USO MCERROJO NI MONS NI SYNCRONIZED NI NADA! )
 *  
 *   PARA QUE ? -> Ademas de simplicidad, al no tener ningun synchronized ent es seguro q no va a haber interbloqueos
 *   ( no tengo q controlar eso yo y cada casso xa q no se de) sin synchronized no hay interbloqueaos, el atomic type class
 *   se encarga auto y asegura sin interbloqueos y encima está optimizado para el > rendimiento! y encima el codigo qda 
 *   muuucho mas sencillo!!! 
 *   
 *   SABER:
 *   CUANDO USAR? SIEMPRE Q SE TRATE DE INCREMENTAR EL VALOR DE UNA VARIABLE , AHI ENTRA ESTA OPCION  100PRE CONVIENE MAS!!!  
 */
public class EJ009_ContConcurrAutoPor_TiposAtomicos {
	
	// https://www.youtube.com/watch?v=ZoTF5U-2GNA&list=PLw8RQJQ8K1ySGcb3ZP66peK4Za0LKf728&index=18

}
