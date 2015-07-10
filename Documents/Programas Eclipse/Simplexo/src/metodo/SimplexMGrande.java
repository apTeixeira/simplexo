package metodo;

import java.text.DecimalFormat;


public class SimplexMGrande {
	
	private int M = 99999;
	private int linha;
	private int coluna;
	private double matriz[][];
	
	private int colunasM = 0;
	private int base[];
	private int nBase[];
	private double pontos[];
	
	private boolean mim = true;
	
	DecimalFormat df = new DecimalFormat("0.##");
	
	public SimplexMGrande(double restricoes[][], double objetivo[], double bis[], int sinal[], boolean mim ) 
	{
		this.mim = mim;

		linha = bis.length;
		coluna = objetivo.length;
		int igual[] =  new int[linha];
		
		
		int auxIgual = 0;
		for(int i = 0; i < linha; i++)
		{
			if(sinal[i] == 1)
			{
				colunasM += 1; 
			}
			else if (sinal[i] == 2)
			{
				igual[auxIgual] = i;
				auxIgual++;
			}
		}
		
		
		matriz = new double[linha + 1][linha + coluna + colunasM + 1];
		int t = linha + coluna + colunasM + 1;
				
		//Acrescenta a função objetivo na matriz
        for (int j = 0; j < coluna; j++) {
        	
        	if(mim)
        		matriz[0][j] = (-1) * objetivo[j];	
        	else
        		matriz[0][j] = objetivo[j];
        }
        
       
        //Acrescenta o M a função objetivo
        int auxM = 0;
        for (int i = 0; i < linha; i++)
        {
        	 if(sinal[i] == 2)
        	{
        		 if(mim)
         			matriz[0][i*2+1] = M;
         		 else
         			matriz[0][i*2+1] = -M;
    		}
       	}
        for (int j = linha + coluna ; j < t-1 ; j++) 
		{	
			   	if(mim)
    			matriz[0][j] = M;
    		else
    			matriz[0][j] = -M;
		}
        
        
        
        //Add bis
		for (int i = 0; i < linha; i++)
		{	
				matriz[i+1][linha+coluna + colunasM] = bis[i];			
		}
		
		
		//Add as variaveis de folga
		int aux = linha;
		for (int i = 0; i < linha; i++)
		{
			if(sinal[i] == 1)
			{
				matriz[i+1][aux + coluna] = 1.0;
				matriz[i+1][coluna+i] = -1.0 ;
				aux+=1;
			}
			else
			{
				matriz[i+1][coluna+i] = 1.0;	
			}
		}
		
		//Acrescenta as restrições
        for (int i = 1; i <= linha; i++)
            for (int j = 0; j < coluna; j++)
                matriz[i][j] = restricoes[i-1][j];

        
		base = new int [bis.length];
		
		int aux2 = 1;
		for(int i = 0; i < linha ; i++)
		{		
			
			if(sinal[i] == 1)
			{
				base[i] = coluna + i + aux2;
				aux2 +=1 ;
			}
			else
				base[i] = coluna + i;				
		}
        
		//Guarda as variaveis não basicas
        nBase = new int [coluna + colunasM];
        for(int i = 0; i < linha; i++)
{		
			
			if(sinal[i] == 1)
			{
				nBase[i] = coluna+i;
			}
			else
				nBase[i] = i;
		//	System.out.println(nBase[i]);
		}
        
   
        //Iniciando o pontos da funcao
        pontos = new double [objetivo.length];
        for(int i = 0; i < coluna; i++) pontos[i] = 0.0;  
        
        resolveSimplex();
        
	}
	
	public void resolveSimplex() 
	{
		int ite = 1;
	
		// Imprimi Matriz
		DecimalFormat df = new DecimalFormat("0.##");
		System.out.println("\nMatriz do Problema:");
		
		for (int i = 0; i <= linha; i++) {
			for (int j = 0; j <= coluna + linha + colunasM; j++)
				System.out.print(df.format(matriz[i][j]) + "\t");
			System.out.println();
		}
		
		while (true) {
			
			System.out.println("\n==========================" + ite + " ITERACAO==========================");
			//Encontra quem entra na base
			int pivo = entraBase();
			// solucao otima
			if (pivo == -1){
				System.out.println("\n==========================Solucao Otima Encontrada==========================");
				//show();
				break; 
			}
				

			// Imprime quem entra na base
			System.out.println("\nEntra na base:\tX" + (pivo + 1) + "\n");

			// Econtra quem sai da base
			int l = saiBase(pivo);
			if (l == -1)
				throw new ArithmeticException("Programa linear é ilimitada");

			// Imprime quem sai da base
			System.out.println("\nSai da base:\tX" + (base[l - 1] + 1) + "\n");

			// Inicia o escolonamento
			escalonarMatriz(l, pivo);

			// Troca a base
			troca(l, pivo);
			

			show();

			ite++;
			
			//limite para o numero de iteracoes
			if (ite > 5)
				break;
		}
				
	}
	public int entraBase() 
	{
	
		
		double valor = 0.0;
		int indice = 0;
		
        for (int i = 0; i <= linha + coluna + colunasM; i++){
        	System.out.println("i: "+i+" "+matriz[0][i] + " valor" + valor);
        	if(matriz[0][i] > valor){
        		valor = matriz[0][i]; 
        		indice = i;
        	}
        }        	
		if(valor > 0)
			return indice;
		
		return -1;
	
	}
	//Escolhe a variavel que sairÃ¡ da base
	public int saiBase(int pivo) 
	{
		double valor = 100000000, temp = 0.0;
		int indice = -1;
		
		for (int i = 1; i <= linha; i++) 
		{	
			if (matriz[i][pivo] >= 0) 
			{		
				temp = (matriz[i][linha + coluna + colunasM] / matriz[i][pivo]);  
				if (temp < valor) 
				{
					valor = temp;
					indice = i;
				}
			}	
		}

		return indice;
	}
	
	//Escalona matriz para obter uma nova forma canonica
	public void escalonarMatriz(int l, int pivo){
		
        for (int i = 0; i <= linha; i++)
            for (int j = 0; j <= linha + coluna + colunasM; j++)
                if (i != l && j != pivo) {
                	matriz[i][j] -=(matriz[l][j] * matriz[i][pivo] / matriz[l][pivo]);
                	System.out.println(matriz[i][j]);
                }
        			

        
        for (int i = 0; i <= linha; i++)
            if (i != l) matriz[i][pivo] = 0.0;

        
        for (int j = 0; j <= linha + coluna + colunasM; j++)
            if (j != pivo) matriz[l][j] /= matriz[l][pivo];
        matriz[l][pivo] = 1.0;
	}
	
	//Tira e coloca variaveis da base
	public void troca(int indice, int pivo){		
		
		int aux = base[indice - 1]; 
		base[indice - 1] = pivo;
		//nBase[pivo] = aux;
		
		if((pivo >= 0) && (pivo < coluna))
			pontos[pivo] = matriz[indice][linha+coluna + colunasM];
	}
	
	//Verifica se PPL esta na forma canonica
	public boolean verificaCompatibilidade(){
			
		for (int i = 0; i <= linha; i++){
            for (int j = 0; j <= coluna + linha + colunasM; j++){
            	if(matriz[i][j] < 0)
            			return false;
            }
                
        }
		
		return true;
	}
	
	public void show(){
		
		//Imprimi Matriz escalonada
		System.out.println("\nMatriz escalonada:");
        for (int i = 0; i <= linha; i++){
            for (int j = 0; j <= coluna + linha + colunasM; j++)
                System.out.print(df.format(matriz[i][j]) + "\t");
            System.out.println();
        }
        
        
        showResult();
	}
	
	public void showResult() {
		if(mim)
			System.out.print("Z =" + df.format(getValue()));
		else
			System.out.print("Z =" + df.format((-1) * getValue()));
		
		System.out.print("\nPonto:(");       
		

				
		 for (int i = 0; i <= linha; i++)
		 {
	        for (int j = 0; j < coluna ; j++)
	        {
					if(matriz[i][j]==1)
					{
						System.out.println(matriz[i][coluna+linha+colunasM]);
						
					}
			}
	        //System.out.println(pontos[i]);
		}
		 System.out.println(")");
	}
	
	public double getValue()
	{
		return matriz[0][linha+coluna + colunasM];
	}
	

	public static void main(String[] args) 
	{
		/*
		 * Parametro da lista de sinais
		 * 0 - Para o sinal de <=
		 * 1 - Para o sinal de >=
		 * 2 - Para o sinal de =
		 */

        //Exemplo
		/*
		 * 
		 
		double[] objetivo = { 5, 2 };
        double[] bis = { 3,   4,   9 };
        int[] sinal =  { 0,   0,   1 };
        double[][] restricoes = {
        					{ 1.0, 0.0},
        					{ 0.0, 1.0},
        					{ 1.0, 2.0},
        				};
        
		
		 */
		double[] objetivo = { 5, 10,  15 };
        double[] bis = { 500,   100,   120 };
        int[] sinal =  { 0,   1,   2 };
        double[][] restricoes = {
        					{ 1.0, 1.0, 1.0},
        					{ 1.0, 1.0, 0.0},
        					{ 1.0, -1.0, -1.0},
        				};
        //*/
        //Última opção indica se é problema de minimização ou não.
        SimplexMGrande exemplo = new SimplexMGrande(restricoes, objetivo, bis, sinal, false);
		
		
	}
}
