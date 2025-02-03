// Arquivo Calendar.java
// Implementação das Classes do Grupo Executivo da Biblioteca de Simulação JAVA
// 19.Mar.1999	Wladimir

package simula;

/**
 * Calendario de Atividades
 */
class Calendar
{
	private TTreeEntry root = null;	// raiz da árvore
	private TTreeEntry list = null;	// lista dos estados ativos correntes

	public Calendar(){}
	/**
	 * Adiciona uma atividade ao calendario
	 */
	public void Add(ActiveState a, double duetime)
	{
		if(root == null)	// se é a primeira inserção
			root = new TTreeEntry(a, duetime);
		else
		{
			TTreeEntry child, parent, e;
			e = new TTreeEntry(a, duetime);

			child  = root;
			parent = null;

			// find insertion point.
			while (child != null) 
			{
				parent = child;

				// Descend left or right sub-trees
				if ((float)duetime < parent.time)
					child = parent.left;
				else if ((float)duetime > parent.time)
					child = parent.right;
				else 
				{
					// Add to list for this node 
					
					// Flag that child is now in middle list 
					e.left = e;
					TTreeEntry y;	// auxiliar

					if (child.middle != null) 
					{
						y = child.middle;
						e.parent = y.parent;
						(y.parent).middle = e;
						y.parent = e;
						e.middle = y;
					}
					else 
					{
						child.middle = e;
						e.right  = child;
						e.parent = e;
						e.middle = e;
					}
					return;
				}
			}

			e.parent = parent;
			// Update parent 
			if ((float)duetime < parent.time)
				parent.left = e;
			else if ((float)duetime > parent.time)
				parent.right = e;
		}
	}
	/**
	 * Remove uma Atividade do calendario
	 */
	public boolean Remove(ActiveState a, double duetime)
	{
		if(root == null)	// se árvore vazia...
			return false;

		TTreeEntry e = Find(a, duetime);

		if(e == null)		// se não encontrou
			return false;

		// remove da lista ligada ao nó

		TTreeEntry x, y;

		x = e.middle;

		if (e.left == e)
		{
			// e is in a list => remove 
			if (e == x) 
			{
				// nothing else in the list 
				(e.right).middle = null;
				e.right = e.middle = e.left = e.parent = null;	// zera referências
				return true;
			}
			else if (e.right != null)
			{
				// e is at the head of the list 
				(e.right).middle = x;
				x.right = e.right;
			}
		
			(e.parent).middle = x;
			x.parent = e.parent;
			e.right = e.middle = e.left = e.parent = null;	// zera referências

			return true;
		}
		else if (x != null) 
		{
			// e has a non-null child list 
			if (x.middle != x) 
			{
				// there is more than one child 
				y = x.middle;
			    (x.parent).middle = y;
				y.parent = x.parent;
				y.right  = x;
			}
			else
				// there is one child 
				x.middle = null;

			x.parent = e.parent;
			x.left   = e.left;
			if (x.left != null)
				(x.left).parent = x;
			x.right  = e.right;
			if (x.right != null)
				(x.right).parent = x;

			// Check if we need to update root 
			if (root == e)
				root = x;
			else if ((e.parent).left == e)
				(e.parent).left = x;
			else
				(e.parent).right = x;
		}
		else 
		{
			// e is in the tree and there is no list 
			// Work out which entity to reposition in the tree. 
			if (e.left != null && e.right != null) 
			{
				for (y = e.right; y.left != null; y = y.left);
			}
			else
				y = e;

			// Set x to a non-null child of y, or null if  
			// there are no non-null children.             
			x = null;
			if (y.left != null)
				x = y.left;
			else
				x = y.right;

			 // Set the pointers for x.
			if (x != null)
				x.parent = y.parent;

			// Set the pointers for y. 
			if (root == y)
				root = x;
			else if (y == (y.parent).left)
				(y.parent).left = x;
			else
				(y.parent).right = x;

			// Insert y in place of entity e. 
			if (y != e) 
			{
				// Update the pointers from e 
				y.parent = e.parent;
				y.left   = e.left;
				y.right  = e.right;

				// Update the pointers to e 
				if (e.left != null)
					(e.left).parent = y;
				if (e.right != null)
					(e.right).parent = y;

				// Update root or parent 
				if (root == e)
					root = y;
				else if ((e.parent).left == e)
					(e.parent).left = y;
				else
					(e.parent).right = y;
			}

			// Invalidate e's old pointers. 
			e.right = null;
			e.left  = null;
			e.parent= null;
			e.middle= null;
		}

		return true;
	}

	/**
	 * encontra posição na árvore
	 * não precisa se preocupar em procurar na lista porque ela só contém 
	 * os eventos que serão executados neste instante
	 */
	private TTreeEntry Find(ActiveState a, double duetime)
	{

		TTreeEntry e;
		e = root;
		while(true)
		{
			if((float)duetime < e.time && e.left != null)
				e = e.left;
			else if((float)duetime > e.time && e.right != null)
				e = e.right;
			else if((float)duetime == e.time)
				break;			
			else
				return null;	// não está na árvore
		}
		if(a == e.a)			// achou!
			return e;
		if(a != e.a && e.middle == null)
			return null;		// tempo certo mas estado ativo não está presente
		
		// procura posição na lista do nó
		TTreeEntry endflag = e.middle.parent;
		while(a != e.a)
		{
			if(e == endflag)	// se sou o último e ainda não encontrei
				return null;	// não existe na árvore
			e = e.middle;	
		}

		return e;
	}

	public ActiveState GetNext()
	{
		if(list == null)		// se o relógio ainda não avançou
			return null;		// não retorna nenhum estado ativo
		
		return list.a;
  
	}
	public float GetNextClock()
	{
		// se este é o primeiro evento após o avanço do relógio
		if(list == null)
		{
			if(root == null)		// se já removeu todas as entidades...
			{
				list = null;
				return (float)0.0;
			}
			
			TTreeEntry x, child, parent;
	
			child = root;
			parent = null;

			// encontra nó com menor duetime
			while (child.left != null) 
			{
				parent = child;
				child  = child.left;
			}
	
			// remodela árvore
			if (child == root)
				root = child.right;
			else 
			{
				parent.left = child.right;
				if (child.right != null)
					(child.right).parent = parent;
			}

			// transforma numa lista duplamente ligada com parent e middle somente
			// e acaba com a lista circular

			if(child.middle != null)
			{
				child.middle.parent.middle = null;	// indica fim da lista
				child.middle.parent = child;		// uniformiza
				child.middle.right = null;			// idem
			}
			child.right = null;					// idem
			child.parent = null;				// desconecta da árvore

			// obtém a lista com os estados ativos a serem servidos neste tempo
			list = child;
		}

		return list.time;
	}
	public boolean RemoveNext()
	{
		if(list == null) return false;	// não cria lista, apenas remove dela
	
		if(list.middle != null)			// se não é último...
		{
			list = list.middle;
			
			// limpa referências 
			list.parent.middle = null;
			list.parent.left = null;
			list.parent = null;			// desconecta da lista
			
			return true;
		}
		else
		{
			list.left = null;
			list = null;		// lista vazia
			
			return false;
		}
	}
}