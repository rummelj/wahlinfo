package com.tu.wahlinfo.benchmark.ConsoleApp;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

	private static final int testRunTimeInMiliseconds = 5 * 60 * 1000;
	private static final Scanner in = new Scanner(System.in);

	public static void main(String[] args) throws InterruptedException {
		Map<String, Float> urls = readUrlsAndProbabilities();
		float averageWaitTime = readAverageWaitTime();
		int numberOfTerminals = readNumberOfTerminals();

		Benchmark benchmark = new Benchmark(urls, averageWaitTime,
				numberOfTerminals);

		System.out.println("Starting benchmark now");
		benchmark.run(testRunTimeInMiliseconds);

		benchmark.printParameters();
		System.out.println("----------------------");
		benchmark.printResult();

		in.close();
	}

	private static int readNumberOfTerminals() {
		while (true) {
			System.out.println("Please enter the number of terminals");
			String time = in.nextLine();
			try {
				return Integer.valueOf(time);
			} catch (Exception e) {
				System.out
						.println("Not a valid whole number, please try again");
			}
		}
	}

	private static float readAverageWaitTime() {
		while (true) {
			System.out.println("Please enter the average wait time in seconds");
			String time = in.nextLine();
			try {
				return Float.valueOf(time);
			} catch (Exception e) {
				System.out.println("Not a valid number, please try again");
			}
		}
	}

	private static Map<String, Float> readUrlsAndProbabilities() {
		System.out.println("Type 'stop' to end input");
		Map<String, Float> result = new HashMap<String, Float>();
		while (true) {
			System.out.println("Please enter a url");
			String url = in.nextLine();

			if (url.isEmpty()) {
				System.out.println("Not a valid url, please try again:");
				continue;
			}

			if (url.equals("stop")) {
				break;
			}

			Float probF = null;
			while (true) {
				System.out.println("Please enter the probability for " + url);
				String prob = in.nextLine();
				if (prob.equals("stop")) {
					System.out
							.println("You can only stop at entering an url, please try again");
					continue;
				}

				try {
					probF = Float.valueOf(prob);
				} catch (Exception e) {
					System.out.println("Invalid number, please try again:");
					continue;
				}

				break;
			}

			result.put(url, probF);
		}

		System.out.println("Recorded " + result.size() + " entries");
		return result;
	}
}
