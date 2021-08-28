package com.example.lab06.dao;

import com.example.lab06.exception.CountryNotFoundException;
import com.example.lab06.model.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class CountryDao {
    private final NamedParameterJdbcTemplate namedTemplate;

    @Autowired
    public CountryDao(NamedParameterJdbcTemplate namedTemplate) {
        this.namedTemplate = namedTemplate;
    }

    public List<Country> getCountryList() {
        return namedTemplate.getJdbcTemplate().query(
                "select * from country",
                new CountryRowMapper());
    }

    public List<Country> getCountryListStartWith(String name) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", name + "%");
        return namedTemplate.query(
                "select * from country where name like :name",
                params,
                new CountryRowMapper());
    }

    public void updateCountryName(String codeName, String newCountryName) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("codeName", codeName);
        params.addValue("newCountryName", newCountryName);
        namedTemplate.update(
                "update country SET name= :newCountryName where code_name= :codeName",
                params);
    }

    public Country getCountryByCodeName(String codeName) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("codeName", codeName);
        return namedTemplate.queryForObject(
                "select * from country where code_name = :codeName",
                params,
                new CountryRowMapper());
    }

    public Country getCountryByName(String name) throws CountryNotFoundException {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", name);
        return Optional.ofNullable(namedTemplate.queryForObject(
                "select * from country where name = :name",
                        params,
                        new CountryRowMapper())
                ).orElseThrow(CountryNotFoundException::new);
    }

    static class CountryRowMapper implements RowMapper<Country> {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String CODE_NAME = "code_name";

        public Country mapRow(ResultSet resultSet, int i) throws SQLException {
            return new Country(
                    resultSet.getInt(ID),
                    resultSet.getString(NAME),
                    resultSet.getString(CODE_NAME));
        }
    }
}
