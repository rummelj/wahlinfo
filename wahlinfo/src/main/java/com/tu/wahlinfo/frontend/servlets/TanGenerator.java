package com.tu.wahlinfo.frontend.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tu.util.Crypto;
import com.tu.util.StringUtil;
import com.tu.wahlinfo.csv.entities.impl.ElectionYear;
import com.tu.wahlinfo.persistence.Database;
import com.tu.wahlinfo.persistence.DatabaseAccessor;
import com.tu.wahlinfo.persistence.DatabaseException;

/**
 * Servlet implementation class TanGenerator
 */
@WebServlet(urlPatterns = "/tanGenerator")
public class TanGenerator extends HttpServlet {

	private static final Logger LOG = LoggerFactory
			.getLogger(TanGenerator.class);

	private static final int TAN_LENGTH = 10;
	// 0 was not used to avoid confusion with O
	private static final String TAN_BASE = "123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	@Inject
	DatabaseAccessor databaseAccessor;

	@Inject
	Database database;

	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TanGenerator() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String year = request.getParameter("year");
		if (year == null) {
			response.getWriter().write("Please specify parameter year!");
			return;
		}
		String electoralDistrict = request.getParameter("electoralDistrict");
		if (electoralDistrict == null) {
			response.getWriter().write(
					"Please specify parameter electoralDistrict!");
			return;
		}
		Integer electoralDistrictNum = 0;
		try {
			electoralDistrictNum = Integer.valueOf(electoralDistrict);
		} catch (NumberFormatException e) {
			response.getWriter()
					.write("Parameter electoralDistrict does not contain a valid number!");
			return;
		}
		String numTans = request.getParameter("numTans");
		if (numTans == null) {
			response.getWriter().write("Please specify parameter numTans!");
			return;
		}
		Integer numTansNum = 0;
		try {
			numTansNum = Integer.valueOf(numTans);
		} catch (NumberFormatException e) {
			response.getWriter().write(
					"Parameter numTans does not contain a valid number!");
			return;
		}

		if (tanGenerationEnabled()) {
			generateTans(ElectionYear.build(year), electoralDistrictNum,
					numTansNum, response);
		} else {
			response.getWriter()
					.write("Tan generation currently disabled please ask the administrator");
		}
	}

	private void generateTans(ElectionYear year, Integer electoralDistrict,
			Integer numTans, HttpServletResponse response) {
		List<String> tans = new ArrayList<String>(numTans);
		for (int i = 0; i < numTans; i++) {
			tans.add(generateTan());
		}
		writeTans(year, electoralDistrict, tans);
		outputTans(year, electoralDistrict, tans, response);
	}

	private void outputTans(ElectionYear year, Integer electoralDistrict,
			List<String> tans, HttpServletResponse response) {
		try {
			response.getWriter().write(
					"<h1>TANs, gültig im Jahr " + year.toCleanString()
							+ " für den Wahlkreis Nummer " + electoralDistrict
							+ "</h1><br/><br/>");

			for (String tan : tans) {
				response.getWriter().write(tan + "<br/>");
			}
		} catch (IOException e) {
			LOG.error("", e);
		}

	}

	private String generateTan() {
		return StringUtil.generateRandomString(TAN_LENGTH, TAN_BASE);
	}

	private boolean tanGenerationEnabled() {
		try {
			String constant = database.getConstants("TanGenerationEnabled");
			if (constant == null) {
				LOG.error("Please add a constants named TanGenerationEnabled to the database with value true to activate tanGeneration. Using false as default for now");
				return false;
			}
			return constant.equals("true");
		} catch (DatabaseException e) {
			LOG.error(
					"Could not determine if tan generation is enabled, so using false as default",
					e);
			return false;
		}
	}

	private void writeTans(ElectionYear year, Integer districtNumber,
			List<String> rawTans) {
		List<String> sha1Tans = new ArrayList<String>(rawTans.size());
		for (String rawTan : rawTans) {
			sha1Tans.add(Crypto.sha1(rawTan));
		}
		Map<String, List<String>> values = new HashMap<String, List<String>>();
		values.put("year",
				createMonoList(year.toCleanString(), sha1Tans.size()));
		values.put("electoralDistrict",
				createMonoList(districtNumber.toString(), sha1Tans.size()));
		values.put("tan", sha1Tans);
		try {
			database.bulkInsert("WITan", values);
		} catch (DatabaseException e) {
			LOG.error("Could not persist tans", e);
		}
	}

	private List<String> createMonoList(String value, int repetitions) {
		List<String> result = new ArrayList<String>(repetitions);
		for (int i = 0; i < repetitions; i++) {
			result.add(value);
		}
		return result;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
