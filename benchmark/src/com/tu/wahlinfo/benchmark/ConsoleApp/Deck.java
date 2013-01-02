package com.tu.wahlinfo.benchmark.ConsoleApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class Deck<T> {

	Random rnd = new Random();
	Map<T, Integer> cardsWithOccurences;
	List<T> cards = new ArrayList<T>();

	public Deck(Map<T, Float> cardsWithProbabilities) {
		cardsWithOccurences = new HashMap<T, Integer>();

		// Find multiplier that makes every float to a whole number brute force
		int i = 1;
		for (;; i++) {
			boolean iIsOk = true;
			for (Float f : cardsWithProbabilities.values()) {
				if (f * i != Math.round(f * i)) {
					iIsOk = false;
					break;
				}
			}
			if (!iIsOk) {
				continue;
			}
			break;
		}

		for (Entry<T, Float> cardWithProbability : cardsWithProbabilities
				.entrySet()) {
			cardsWithOccurences.put(cardWithProbability.getKey(),
					Math.round(cardWithProbability.getValue() * i));
		}
	}

	public T drawCard() {
		if (cards.isEmpty()) {
			initCards();
		}
		return selectRandomCard();
	}

	private void initCards() {
		for (Entry<T, Integer> cardEntry : cardsWithOccurences.entrySet()) {
			T card = cardEntry.getKey();
			for (int i = 0; i < cardEntry.getValue(); i++) {
				cards.add(card);
			}
		}
	}

	private T selectRandomCard() {
		int index = rnd.nextInt(cards.size());
		T card = cards.get(index);
		cards.remove(index);
		return card;
	}

}
