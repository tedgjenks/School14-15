package edu.cb.lunch.guareschi.alex;


import edu.jenks.dist.cb.lunch.*;

public class Trio extends AbstractTrio {

	public Trio(Sandwich sandwich, Salad salad, Drink drink) {
		super(sandwich, salad, drink);

		
	}

	@Override
	public String getName() {
		String trioName = "";
		Sandwich sandwich = getSandwich();
		String sandwichName = sandwich.getName();
		Salad salad = getSalad();
		String saladName = salad.getName();
		Drink drink = getDrink();
		String drinkName = drink.getName();
		trioName= (sandwichName + "/" + saladName + "/" + drinkName + " Trio");
		return trioName;
	}

	@Override
	public double getPrice() {
		double price = 0.00d;
		Sandwich sandwich = getSandwich();
		double sandwichPrice= sandwich.getPrice();
		Salad salad = getSalad();
		double saladPrice= salad.getPrice();
		Drink drink = getDrink();
		double drinkPrice= drink.getPrice();
		double price1 = sandwichPrice + saladPrice;
		double price2 = sandwichPrice + drinkPrice;
		double price3 = saladPrice+drinkPrice;
		if (price1 >= price2 && price1>= price3){
			return price1;
		}
		if (price2 >= price1 && price2>=price3){
			return price2;
		}
		if (price3>=price1 && price3>=price2){
			return price3;
			
		}
		

		return price;
	}

}