package com.a.n.smartgym.multicolumnlistview;

/**
 * 
 * @author anfer
 * 
 */
public class Model {

	private String exercise_name;
	private String category;
	private String number_sets;
	private String number_reps;
	private String weight;

	public Model(String exercise_name, String category, String number_sets, String number_reps, String weight) {
		this.exercise_name = exercise_name;
		this.category = category;
		this.number_sets = number_sets;
		this.number_reps = number_reps;
		this.weight = weight;
	}

	public String getExercise_name() {
		return exercise_name;
	}

	public void setExercise_name(String exercise_name) {
		this.exercise_name = exercise_name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getNumber_sets() {
		return number_sets;
	}

	public void setNumber_sets(String number_sets) {
		this.number_sets = number_sets;
	}

	public String getNumber_reps() {
		return number_reps;
	}

	public void setNumber_reps(String number_reps) {
		this.number_reps = number_reps;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}
}
