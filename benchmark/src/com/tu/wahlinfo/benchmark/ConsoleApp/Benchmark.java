package com.tu.wahlinfo.benchmark.ConsoleApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Benchmark {

	Map<String, Float> urls;
	float averageWaitTime;
	int numberOfTerminals;

	boolean threadsShouldStop;

	Map<String, List<Long>> results;

	public Benchmark(Map<String, Float> urls, float averageWaitTime,
			int numberOfTerminals) {
		this.urls = urls;
		this.averageWaitTime = averageWaitTime;
		this.numberOfTerminals = numberOfTerminals;
	}

	public void run(int testruntimeinmiliseconds) throws InterruptedException {
		initResults();
		threadsShouldStop = false;
		List<Thread> threads = startThreads(numberOfTerminals);
		Thread.sleep(testruntimeinmiliseconds);
		threadsShouldStop = true;
		for (Thread thread : threads) {
			thread.join();
		}
	}

	private List<Thread> startThreads(int noOfThreads) {
		List<Thread> threads = new ArrayList<Thread>(noOfThreads);
		for (int i = 0; i < noOfThreads; i++) {
			Thread t = new Thread() {
				public void run() {
					try {
						threadCode();
					} catch (IOException e) {
						System.out.println(e);
					}
				}
			};
			threads.add(t);
			t.start();
		}
		return threads;
	}

	void threadCode() throws IOException {
		Deck<String> deck = new Deck<String>(urls);
		while (!threadsShouldStop) {
			String url = deck.drawCard();
			long timeBefore = new Date().getTime();
			callUrl(url);
			long timeAfter = new Date().getTime();
			writeResult(url, (timeAfter - timeBefore));
			System.out.println(url + " answered after "
					+ (timeAfter - timeBefore) + "ms");
			try {
				Thread.sleep((long) (averageWaitTime * 1000));
			} catch (InterruptedException e) {
				System.out.println("Interrupted");
			}
		}
	}

	private void callUrl(String url) throws IOException {
		URL urlObj = new URL(url);
		BufferedReader in = new BufferedReader(new InputStreamReader(
				urlObj.openStream()));

		// Fake output
		while ((in.readLine()) != null) {
		}
		in.close();
	}

	void initResults() {
		results = new HashMap<String, List<Long>>();
		for (String url : urls.keySet()) {
			results.put(url, new LinkedList<Long>());
		}
	}

	synchronized void writeResult(String url, long timeTaken) {
		results.get(url).add(timeTaken);
	}

	public void printParameters() {
		System.out.println("Benchmark used the following parameters");
		System.out.println("---------------------------------------");
		System.out.println("");
		System.out.println("URL - Probability");
		System.out.println("-----------------");
		System.out.println("");
		for (Entry<String, Float> urlEntry : urls.entrySet()) {
			System.out.println(urlEntry.getKey() + " - " + urlEntry.getValue());
		}
		System.out.println("");
		System.out.println("Average wait time between calls: "
				+ averageWaitTime + " seconds");
		System.out.println("Number of terminals: " + numberOfTerminals);
	}

	public void printResult() {
		Map<String, Float> averages = new HashMap<String, Float>();
		for (Entry<String, List<Long>> resultEntry : results.entrySet()) {
			long totalTime = 0L;
			for (Long l : resultEntry.getValue()) {
				totalTime += l;
			}
			averages.put(
					resultEntry.getKey(),
					(float) ((float) totalTime / resultEntry.getValue().size()) / 1000);
		}

		System.out
				.println("Benchmark produced the following average load times");
		System.out
				.println("---------------------------------------------------");
		for (Entry<String, Float> average : averages.entrySet()) {
			System.out.println(average.getKey() + ": " + average.getValue()
					+ " seconds");
		}
	}
}
