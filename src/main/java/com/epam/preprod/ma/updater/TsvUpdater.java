package com.epam.preprod.ma.updater;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.com.bytecode.opencsv.CSVReader;

import com.epam.preprod.ma.bean.RowBean;
import com.epam.preprod.ma.constant.EMarker;
import com.epam.preprod.ma.dao.entity.Role;
import com.epam.preprod.ma.dao.entity.Unit;
import com.epam.preprod.ma.dao.entity.User;
import com.epam.preprod.ma.service.interf.IUnitService;
import com.epam.preprod.ma.service.interf.IUpdaterService;
import com.epam.preprod.ma.service.interf.IUserService;

/**
 * <p>
 * Imports organization structure from .tsv file and update database according
 * to new data.
 * </p>
 * <p>
 * WARN! Works only with 'wl_export' file in such format: <br />
 * <code>
 * 'FULLNAME' | 'EMPLOYEE_ID' | 'MANAGER_ID' | 'MANAGER_NAME' | 'UNIT_ID' | 'UNIT_NAME' |
 * 'EMAIL_ADDRESS' | 'PERIOD_START_DATE' (...)
 * </code>
 * </p>
 * 
 * @author Oleksandr Lagoda
 * 
 * @version 1.0
 * 
 */
@Component("tsvUpdater")
public class TsvUpdater implements IDatabaseUpdater, Runnable {

	private static final Logger LOGGER = Logger.getLogger(TsvUpdater.class);

	private String filepath;

	private boolean deleteFile;

	@Autowired
	private IUserService userService;

	@Autowired
	private IUnitService unitService;

	@Autowired
	private IUpdaterService updaterService;

	/**
	 * Actual system date (month and year).
	 */
	private Calendar currentDate;

	@Override
	public void run() {

		try {

			// Container of units to be uploaded to database. Value in map tells
			// update or delete such user in database.
			Map<Unit, EMarker> units = new HashMap<>();

			// Container of users to be uploaded to database. Value in map tells
			// update or delete such user in database.
			Map<User, EMarker> users = new HashMap<>();

			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Database update started. TSV Updater in progress.");
				LOGGER.info("Start reading '" + filepath + "' file.");
			}

			processFile(units, users);

			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Reading file finished.");
				LOGGER.info("Start uploading data to database.");
			}

			updaterService.update(users, units);

			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Data uploading finished.");
				LOGGER.info("Database update finished.");
			}
		} catch (RuntimeException runtimeException) {

			LOGGER.error("Runtime error occured", runtimeException);

		} finally {
			// delete source file if flag set to true.
			if (deleteFile) {

				LOGGER.trace("Deleting source file.");

				new File(filepath).delete();
			}
		}
	}

	/**
	 * Fill in database with info from .tsv file.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	@Override
	public void update(String filepath, boolean deleteFile) {
		try {
			this.filepath = filepath;
			this.deleteFile = deleteFile;

			Thread thread = new Thread(this);
			thread.setName("tsv-updater");
			thread.start();
		} catch (RuntimeException runtimeException) {
			LOGGER.error("Runtime error occured", runtimeException);
		}
	}

	/**
	 * Read data from file. Fill {@link #units} and {@link #users} containers
	 * with data from it.
	 * 
	 * @param users
	 *            - list of {@linkplain User}'s with specified
	 *            {@linkplain EMarker}
	 * @param units
	 *            - list of {@linkplain Unit}'s with specified
	 *            {@linkplain EMarker}
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private void processFile(Map<Unit, EMarker> units, Map<User, EMarker> users) {

		try {

			// Users currently stores in database.
			List<User> currentUsers = userService.readAllUsers();
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Users id currently stored in database: "
						+ currentUsers);
			}

			// Units currently stores in database.
			List<Unit> currentUnits = unitService.readAllUnits();
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Units id currently stored in database: "
						+ currentUnits);
			}

			// List of all RM's read from file.
			Set<User> managers = new HashSet<>();

			// Attitude of employees to theirs managers.
			Map<User, User> usersToManagers = new HashMap<User, User>();

			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Rows processing started.");
			}

			readDataFromFile(managers, usersToManagers, units, currentUnits,
					users, currentUsers);

			updateUsersRole(users, currentUsers, managers);

			addRecentUsers(users, currentUsers, managers);

			establishUnitsRM(units, users, managers);
			establishParentUnits(units, users, usersToManagers);

			checkForOutdateUsers(users, currentUsers);
			checkForOutdateUnits(units, currentUnits);

			/** --- Rows processing finished --- */

			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Rows processing finished");
			}

		} catch (IOException e) {
			String message = "Error reading file.";
			LOGGER.error(message, e);
		}
	}

	/**
	 * Read all date from file specified in {@link #filepath} variable and fill
	 * users, units, managers and usersToManagers containers with data which has
	 * been read from file.
	 * 
	 * @param managers
	 *            - list of all RM's in company.
	 * @param usersToManagers
	 *            - list of attitude of employees to theirs managers.
	 * @param units
	 *            - list of all {@linkplain Unit}'s which has been read from
	 *            file.
	 * @param currentUnits
	 *            - list of current {@linkplain Unit}'s.
	 * @param users
	 *            - list of all {@linkplain Users}'s which has been read from
	 *            file.
	 * @param currentUsers
	 *            - list of current {@linkplain Users}'s.
	 * 
	 * @throws IOException
	 *             When reading file problem occurred.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private void readDataFromFile(Set<User> managers,
			Map<User, User> usersToManagers, Map<Unit, EMarker> units,
			List<Unit> currentUnits, Map<User, EMarker> users,
			List<User> currentUsers) throws IOException {

		CSVReader csvReader = initializeCsvReader(filepath);

		// Not used. Just to read header row.
		@SuppressWarnings("unused")
		String[] headersRow = csvReader.readNext();

		// Previous row read form file.
		RowBean previousRow = new RowBean(csvReader.readNext());

		// Current row read form file.
		RowBean currentRow = new RowBean(csvReader.readNext());

		// Newest row from two assigned to one user.
		RowBean newestRow = previousRow;

		// Initialize current date.
		currentDate = getCurrentDate();

		// Read to the end of file.
		while (!currentRow.isEmpty()) {

			newestRow = getNewestRow(previousRow, newestRow);

			// If all info assigned to one user was read from file.
			if (!previousRow.getEmployeeId().equals(currentRow.getEmployeeId())) {

				addInitialData(managers, newestRow, usersToManagers, units,
						currentUnits, users, currentUsers);
				newestRow = null;
			}

			previousRow = currentRow;
			currentRow = new RowBean(csvReader.readNext());
		}

		csvReader.close();
	}

	/**
	 * Add data from concrete file row to users, units, managers and
	 * usersToManagers containers.
	 * 
	 * @param managers
	 *            - list of all RM's in company.
	 * @param infoRow
	 *            - row with data.
	 * @param usersToManagers
	 *            - list of attitude of employees to theirs managers.
	 * @param units
	 *            - list of all {@linkplain Unit}'s which has been read from
	 *            file.
	 * @param currentUnits
	 *            - list of current {@linkplain Unit}'s.
	 * @param users
	 *            - list of all {@linkplain Users}'s which has been read from
	 *            file.
	 * @param currentUsers
	 *            - list of current {@linkplain Users}'s.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private void addInitialData(Set<User> managers, RowBean infoRow,
			Map<User, User> usersToManagers, Map<Unit, EMarker> units,
			List<Unit> currentUnits, Map<User, EMarker> users,
			List<User> currentUsers) {

		addUserToManager(usersToManagers, infoRow);

		User employee = createEmployee(infoRow);
		Unit unit = createUnit(infoRow);

		if (isRowOutOfDate(infoRow)) {
			clearUserData(employee);
			clearUnitData(unit);
		} else {
			addManager(managers, infoRow);
		}

		if (unit != null) {
			addUnit(units, currentUnits, unit);
		}
		if (employee != null) {
			addUser(users, currentUsers, employee);
		}
	}

	/**
	 * Add {@linkplain User} and his manager which gets from received
	 * {@linkplain RowBean} to usersToManagers container.
	 * 
	 * @param usersToManagers
	 *            - container to which data will be added.
	 * @param newestRow
	 *            - {@linkplain RowBean} with data.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private void addUserToManager(Map<User, User> usersToManagers,
			RowBean newestRow) {

		User employee = new User();
		User manager = new User();

		employee.setId(newestRow.getEmployeeId());
		manager.setId(newestRow.getManagerId());

		usersToManagers.put(employee, manager);
	}

	/**
	 * Add new {@linkplain User} as RM to managers list.
	 * 
	 * @param managers
	 *            - list to which {@linkplain User} must be added.
	 * @param infoRow
	 *            - {@linkplain RowBean} with {@linkplain User} data.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private void addManager(Set<User> managers, RowBean infoRow) {

		User manager = new User();

		Long managerId = infoRow.getManagerId();

		if (managerId == null) {
			return;
		}

		manager.setId(managerId);
		manager.setName(infoRow.getManagerName());
		manager.setRole(Role.RM);

		managers.add(manager);
	}

	/**
	 * Create {@linkplain Unit} entity with predefined {@linkplain Unit#id},
	 * {@linkplain Unit#managerId} and {@linkplain Unit#name} fields.
	 * 
	 * @param infoRow
	 *            - row from which data will be selected to {@linkplain Unit}
	 *            object.
	 * 
	 * @return {@linkplain Unit} entity with predefined values.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private Unit createUnit(RowBean infoRow) {
		Unit unit = new Unit();

		if (infoRow.getUnitId() == null) {
			return null;
		}

		unit.setId(infoRow.getUnitId());
		unit.setName(infoRow.getUnitName());

		unit.setDeleted(false);
		return unit;
	}

	/**
	 * Create {@linkplain User} entity with predefined fields.
	 * 
	 * @param infoRow
	 *            - row from which data will be selected to {@linkplain User}
	 *            object.
	 * 
	 * @return {@linkplain User} entity with predefined values.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private User createEmployee(RowBean infoRow) {
		if (infoRow.getEmployeeId() == null) {
			return null;
		}

		User employee = new User();

		employee.setId(infoRow.getEmployeeId());
		employee.setName(infoRow.getEmployeeName());

		employee.setEmail(infoRow.getEmail());
		employee.setRole(Role.EMPLOYEE);

		Unit unit = new Unit();
		unit.setId(infoRow.getUnitId());
		employee.setUnit(unit);

		employee.setDeleted(false);

		return employee;
	}

	/**
	 * Add new {@linkplain User} to users with predefined role and marker.
	 * 
	 * @param user
	 *            - {@linkplain User} to add.
	 * 
	 * @param marker
	 *            - {@linkplain EMarker} with which user must be added.
	 * 
	 * @param users
	 *            - list to which {@linkplain User} must be added.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private void addUser(Map<User, EMarker> users, User user, Role role,
			EMarker marker) {
		user.setRole(role);
		users.put(user, marker);
	}

	/**
	 * Add new object to users container.
	 * 
	 * @param currentUsers
	 *            - list of units currently stored in database.
	 * @param user
	 *            - {@linkplain User} object to add.
	 * 
	 * @param users
	 *            - list to which {@linkplain User} must be added.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private void addUser(Map<User, EMarker> users, List<User> currentUsers,
			User user) {
		if (currentUsers.contains(user)) {
			checkSuperadminRole(user, currentUsers);
			users.put(user, EMarker.UPDATE);
		} else {
			users.put(user, EMarker.CREATE);
		}
	}

	/**
	 * Check if received user is 'superadmin'.
	 * 
	 * @param user
	 *            - {@linkplain User} object to check.
	 * 
	 * @param currentUsers
	 *            - list of units currently stored in database.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private void checkSuperadminRole(User user, List<User> currentUsers) {
		for (User currentUser : currentUsers) {
			if (currentUser.equals(user)) {
				if (currentUser.getRole() == Role.SUPERADMIN) {
					user.setRole(Role.SUPERADMIN);
				}
			}
		}

	}

	/**
	 * Add new object to {@link #units} container.
	 * 
	 * @param currentUnits
	 *            - list of units currently stored in database.
	 * @param unit
	 *            - {@linkplain Unit} object to add.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private void addUnit(Map<Unit, EMarker> units, List<Unit> currentUnits,
			Unit unit) {
		if (currentUnits.contains(unit)) {
			units.put(unit, EMarker.UPDATE);
		} else {
			units.put(unit, EMarker.CREATE);
		}
	}

	/**
	 * Add managers which is not currently stored in users container.
	 * 
	 * @param users
	 *            - list of {@linkplain Users}'s which has been previously read
	 *            from file.
	 * @param currentUsers
	 *            - - list of @linkplain Users}'s currently stores in database.
	 * @param managers
	 *            - list of managers to add.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private void addRecentUsers(Map<User, EMarker> users,
			List<User> currentUsers, Set<User> managers) {
		for (User user : managers) {
			if (!users.containsKey(user)) {
				addUser(users, currentUsers, user);
			}
		}
	}

	/**
	 * Set {@link User#email}, {@link User#role} and {@link User#unit} fields to
	 * null and {@link User#deleted} to true.
	 * 
	 * @param user
	 *            - {@linkplain User} in which fields will be set.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private void clearUserData(User user) {
		if (user == null) {
			return;
		}
		user.setEmail(null);
		user.setRole(null);
		user.setUnit(null);
		user.setDeleted(true);
	}

	/**
	 * Set {@link Unit#parent} and {@link Unit#rm} fields to null and
	 * {@link Unit#deleted} to true.
	 * 
	 * @param unit
	 */
	private void clearUnitData(Unit unit) {
		if (unit == null) {
			return;
		}
		unit.setParent(null);
		unit.setRm(null);
		unit.setDeleted(true);
	}

	/**
	 * Sets for all {@linkplain Unit}'s in units container theirs managers.
	 * 
	 * @param units
	 *            - container of {@linkplain Unit}'s.
	 * @param users
	 *            - container of {@linkplain User}'s
	 * @param managers
	 *            - container of managers.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private void establishUnitsRM(Map<Unit, EMarker> units,
			Map<User, EMarker> users, Set<User> managers) {

		for (User manager : managers) {

			Entry<User, EMarker> managerEntry = getUserWithMarker(users,
					manager);

			if (managerEntry != null) {

				Unit managerUnit = managerEntry.getKey().getUnit();

				if (managerUnit != null) {

					updateUnitManager(managerUnit, units, manager);

				} else {
					LOGGER.trace("No such unit found.");
				}

			} else {
				LOGGER.trace("No such user found.");
			}
		}
	}

	/**
	 * Update {@linkplain Unit#rm} field in received {@linkplain Unit}.
	 * 
	 * @param unit
	 *            - {@linkplain Unit} to update.
	 * @param units
	 *            - container which contains {@linkplain Unit} to update.
	 * @param manager
	 *            - manager of {@linkplain Unit} that will be updated.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private void updateUnitManager(Unit unit, Map<Unit, EMarker> units,
			User manager) {

		Entry<Unit, EMarker> unitEntry = getUnitWithMarker(units, unit);
		unit = unitEntry.getKey();
		EMarker marker = unitEntry.getValue();

		units.remove(unit);

		unit.setRm(manager);

		units.put(unit, marker);
	}

	/**
	 * Set parent {@linkplain Unit}'s fields by searching it in
	 * {@linkplain Users}'s which have {@linkplain Role} equals to 'RM'.
	 * 
	 * @param units
	 *            - container which contains all {@linkplain Unit}'s.
	 * @param users
	 *            - container which contains all {@linkplain User}'s.
	 * @param usersToManagers
	 *            - list of attitude of employees to theirs managers.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private void establishParentUnits(Map<Unit, EMarker> units,
			Map<User, EMarker> users, Map<User, User> usersToManagers) {

		Map<Unit, EMarker> updatedUnits = new HashMap<>();
		User parentManager = null;
		Unit parentUnit = null;

		for (Entry<Unit, EMarker> unitEntry : units.entrySet()) {
			Unit currentUnit = unitEntry.getKey();
			EMarker currentUnitMarker = unitEntry.getValue();

			User manager = currentUnit.getRm();

			// If current unit has it's own manager then set those manager unit
			// as parent to current otherwise search for any user from this unit
			// and set his manager unit as parent to current. In case if unit
			// has no users then it's parent unit will be set as null.
			if (manager != null) {

				parentManager = usersToManagers.get(manager);

				Entry<User, EMarker> parentManagerEntry = getUserWithMarker(
						users, parentManager);
				parentUnit = parentManagerEntry.getKey().getUnit();

			} else {

				User user = getFirstUserFormUnit(users, currentUnit);

				parentManager = usersToManagers.get(user);

				if (parentManager != null) {
					Entry<User, EMarker> parentManagerEntry = getUserWithMarker(
							users, parentManager);
					parentUnit = parentManagerEntry.getKey().getUnit();
				}
			}

			currentUnit.setParent(parentUnit);

			updatedUnits.put(currentUnit, currentUnitMarker);

			parentUnit = null;
		}

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Updated units.");
			for (Unit unit : updatedUnits.keySet()) {
				LOGGER.trace(unit);
			}
		}

		units = updatedUnits;
	}

	/**
	 * Finds first {@linkplain User} in users container which has the same
	 * {@linkplain Unit} as received.
	 * 
	 * @param users
	 *            - container which contains all {@linkplain User}'s.
	 * @param unit
	 *            - {@linkplain Unit} to search in {@linkplain User}.
	 * 
	 * @return First {@linkplain User} from unit.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private User getFirstUserFormUnit(Map<User, EMarker> users, Unit unit) {
		for (User user : users.keySet()) {
			Unit userUnit = user.getUnit();
			if (userUnit != null && userUnit.equals(unit)) {
				return user;
			}
		}
		return null;
	}

	/**
	 * Finds concrete {@linkplain User} with {@linkplain EMarker} in
	 * {@link #users}
	 * 
	 * @param user
	 *            - {@linkplain User} to search for.
	 * 
	 * @param users
	 *            - container which contains all {@linkplain User}'s.
	 * 
	 * @return {@linkplain User} with it current {@linkplain EMarker} or null if
	 *         {@link #users} is not contains such.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private Entry<User, EMarker> getUserWithMarker(Map<User, EMarker> users,
			User user) {

		for (Entry<User, EMarker> entry : users.entrySet()) {
			User tempUser = entry.getKey();

			if (tempUser.equals(user)) {
				return entry;
			}
		}

		return null;
	}

	/**
	 * Finds concrete{@linkplain Unit} with {@linkplain EMarker} in
	 * {@link #units}
	 * 
	 * @param unit
	 *            - {@linkplain Unit} to search for.
	 * 
	 * @param units
	 *            - container which contains all {@linkplain Unit}'s.
	 * 
	 * @return {@linkplain Unit} with it current {@linkplain EMarker} or null if
	 *         {@link #units} is not contains such.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private Entry<Unit, EMarker> getUnitWithMarker(Map<Unit, EMarker> units,
			Unit unit) {

		for (Entry<Unit, EMarker> unitEntry : units.entrySet()) {
			if (unitEntry.getKey().equals(unit)) {
				return unitEntry;
			}
		}

		return null;
	}

	/**
	 * Update received {@linkplain User} role in {@link #users}.
	 * 
	 * @param managers
	 *            - data with {@linkplain User} to update.
	 * 
	 * @param users
	 *            - container which contains all {@linkplain User}'s.
	 * 
	 * @param currentUsers
	 *            - container of {@linkplain User}'s currently stored in
	 *            database.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private void updateUsersRole(Map<User, EMarker> users,
			List<User> currentUsers, Set<User> managers) {

		for (User manager : managers) {
			Entry<User, EMarker> userEntry = getUserWithMarker(users, manager);
			User user = null;
			EMarker marker = null;

			if (userEntry != null) {
				user = userEntry.getKey();
				marker = userEntry.getValue();

				if (!user.isDeleted() && user.getRole() != Role.SUPERADMIN) {
					users.remove(user);
					user.setRole(Role.RM);
					users.put(user, marker);
				}
			} else {
				user = new User();
				user.setId(manager.getId());
				user.setName(manager.getName());
				user.setRole(Role.RM);
				addUser(users, currentUsers, user);
			}
		}
	}

	/**
	 * Select newest {@linkplain RowBean} from two.
	 * 
	 * @param firstRow
	 *            - row to compare.
	 * @param secondRow
	 *            - row to compare.
	 * 
	 * @return Newest row.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private RowBean getNewestRow(RowBean firstRow, RowBean secondRow) {
		if (firstRow == null) {
			return secondRow;
		} else if (secondRow == null) {
			return firstRow;
		} else if (firstRow.getDate().before(secondRow.getDate())) {
			return secondRow;
		} else {
			return firstRow;
		}
	}

	/**
	 * Check if received row older then 1st day of current month.
	 * 
	 * @param infoRow
	 *            - row to be checked.
	 * 
	 * @return True if row is outdated.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private boolean isRowOutOfDate(RowBean infoRow) {

		// If rowDate will be set as 00:00:00 and currentDate will be set in
		// same way then Calendar.before() will returned true. Because of that
		// we add one second to avoid this inconvenience.
		Calendar rowDate = infoRow.getDate();
		rowDate.set(Calendar.SECOND, rowDate.get(Calendar.SECOND) + 1);

		return infoRow.getDate().before(currentDate);
	}

	/**
	 * Check if all users was proceed. If not marked it as deleted and added to
	 * {@link #users} container.
	 * 
	 * @param currentUsers
	 *            - list of users to be checked.
	 * 
	 * @param users
	 *            - container which contains all {@linkplain User}'s.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private void checkForOutdateUsers(Map<User, EMarker> users,
			List<User> currentUsers) {
		for (User user : currentUsers) {
			if (!users.containsKey(user)) {
				clearUserData(user);
				addUser(users, user, user.getRole(), EMarker.UPDATE);
			}
		}
	}

	/**
	 * Check if all units was proceed. If not marked it as deleted and added to
	 * {@link #units} container.
	 * 
	 * @param currentUnits
	 *            - list of units to be checked.
	 * 
	 * @param units
	 *            - container which contains all {@linkplain Unit}'s.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private void checkForOutdateUnits(Map<Unit, EMarker> units,
			List<Unit> currentUnits) {
		for (Unit unit : currentUnits) {
			if (!units.containsKey(unit)) {
				clearUnitData(unit);
				units.put(unit, EMarker.UPDATE);
			}
		}
	}

	/**
	 * Retrieve current date and set first day of month on it with 00:00:00
	 * time.
	 * 
	 * @return {@linkplain Calendar} instance with predefined settings.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private Calendar getCurrentDate() {
		Calendar currentDate = GregorianCalendar.getInstance();

		currentDate.set(Calendar.DAY_OF_MONTH, 1);
		currentDate.set(Calendar.HOUR_OF_DAY, 0);
		currentDate.set(Calendar.MINUTE, 0);
		currentDate.set(Calendar.SECOND, 0);

		return currentDate;
	}

	/**
	 * Create new {@linkplain CVSReader} according to filename.
	 * 
	 * @param tsvFilepath
	 *            - full filename.
	 * 
	 * @return {@linkplain CVSReader} object attached to file.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private CSVReader initializeCsvReader(String tsvFilepath) {

		CSVReader csvReader = null;

		try {

			csvReader = new CSVReader(new FileReader(tsvFilepath), '\t');

		} catch (FileNotFoundException e) {

			String message = "File with the specified pathname does not exist. Database is not updated.";

			LOGGER.error(message, e);
		}

		return csvReader;
	}
}