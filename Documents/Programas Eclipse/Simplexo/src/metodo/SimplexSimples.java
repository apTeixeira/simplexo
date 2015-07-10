package metodo;

import java.text.DecimalFormat;

public class SimplexSimples {

	private int linha;
	private int coluna;
	private double matriz[][];
	
	private int base[];
	private int nBase[];
	private double pontos[];
	
	private boolean mim = true;
	
	DecimalFormat df = new DecimalFormat("0.##");
	
	public SimplexSimples(double restricoes[][], double objetivo[], double bis[], boolean mim ) {
		
		this.mim = mim;
		
		linha = bis.length;
		coluna = objetivo.length;
		
		matriz = new double[linha + 1][linha + coluna + 1];
	    
				
		//Acrescenta a função objetivo na matriz
        for (int j = 0; j < coluna; j++) {
        	
        	if(mim)
        		matriz[0][j] = (-1) * objetivo[j];	
        	else
        		matriz[0][j] = objetivo[j];
        }
        

        //Add bis
		for (int i = 0; i < linha; i++)
		{	
				matriz[i+1][linha+coluna] = bis[i];			
		}
		
		
		//Add as variaveis de folga
		for (int i = 0; i < linha; i++)
		{
			matriz[i+1][coluna+i] = 1.0;
		}
               

        //Acrescenta as restrições
        for (int i = 1; i <= linha; i++)
            for (int j = 0; j < coluna; j++)
                matriz[i][j] = restricoes[i-1][j];
		
        
        //Guarda as variaveis de folga como base
        base = new int [bis.length];
		for(int i = 0; i < linha; i++)
		{
			base[i] = coluna + i;
		}
        
		//Guarda as variaveis não basicas
        nBase = new int [objetivo.length];
        for(int i = 0; i < coluna; i++) nBase[i] = i;
        
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
			for (int j = 0; j <= coluna + linha; j++)
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
		
        for (int i = 0; i <= linha + coluna; i++){
        	
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
				temp = (matriz[i][linha + coluna] / matriz[i][pivo]);  
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
            for (int j = 0; j <= linha + coluna; j++)
                if (i != l && j != pivo) 
                	matriz[i][j] -= matriz[l][j] * matriz[i][pivo] / matriz[l][pivo];

        
        for (int i = 0; i <= linha; i++)
            if (i != l) matriz[i][pivo] = 0.0;

        
        for (int j = 0; j <= linha + coluna; j++)
            if (j != pivo) matriz[l][j] /= matriz[l][pivo];
        matriz[l][pivo] = 1.0;
	}
	
	//Tira e coloca variaveis da base
	public void troca(int indice, int pivo){		
		
		int aux = base[indice - 1]; 
		base[indice - 1] = pivo;
		//nBase[pivo] = aux;
		
		if((pivo >= 0) && (pivo < coluna))
			pontos[pivo] = matriz[indice][linha+coluna];
	}
	
	//Verifica se PPL esta na forma canonica
	public boolean verificaCompatibilidade(){
			
		for (int i = 0; i <= linha; i++){
            for (int j = 0; j <= coluna + linha; j++){
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
            for (int j = 0; j <= coluna + linha; j++)
                System.out.print(df.format(matriz[i][j]) + "\t");
            System.out.println();
        }
        
        System.out.println("\nVariaveis Basicas\n"); 
        //Imprimi variaveis basica
        for(int i = 0; i < linha; i++)
        	System.out.println("X" + (base[i]+1) + ":\t" + matriz[i+1][linha+coluna] + "\n");
        
		
        showResult();
	}
	
	public void showResult() {
		if(mim)
			System.out.print("Z =" + df.format(getValue()));
		else
			System.out.print("Z =" + df.format((-1) * getValue()));
		
		System.out.print(" -> P:(");
		for (int i = 0; i < coluna; i++) {
			if(i+1 == coluna)
				System.out.print((pontos[i] +")\n"));
			else
				System.out.print((pontos[i] +"\t"));
		}
	}
	
	public double getValue()
	{
		return matriz[0][linha+coluna];
	}
	
	
	public static void main(String[] args) 
	{
		

        //Exemplo
		double[] objetivo = { 5, 2 };
        double[] bis = { 3,   4,   9 };
        double[][] restricoes = {
        					{ 1.0, 0.0},
        					{ 0.0, 1.0},
        					{ 1.0, 2.0},
        				};
        
        
        //Última opção indica se é problema de minimização ou não.
        SimplexSimples exemplo = new SimplexSimples(restricoes, objetivo, bis, false);
		
		
	}
}
