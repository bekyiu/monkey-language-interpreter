let Person = fn(name, age, job) {

    let profile = fn(this) {
        puts(
            "My name is " + this["name"] + ", I am " + this["age"] + "'s old",
            "My job is " + this["job"]
        )
    }

    return {
        "name": name,
        "age": age,
        "job": job,
        "profile": profile
    }
}

let nanase = Person("nishino nanase", "27", "actress")
nanase["profile"](nanase)