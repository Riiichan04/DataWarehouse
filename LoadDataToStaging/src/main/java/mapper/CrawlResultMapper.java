package mapper;

import models.CrawlResult;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CrawlResultMapper implements RowMapper<CrawlResult> {
    @Override
    public CrawlResult map(ResultSet rs, StatementContext ctx) throws SQLException {
        CrawlResult crawlResult = new CrawlResult();
        crawlResult.setDate(rs.getString("date"));
        crawlResult.setPrizeName(rs.getString("prizeName"));
        crawlResult.setCompanyName(rs.getString("companyName"));
        crawlResult.setResult(rs.getString("result"));
        crawlResult.setCreatedAt(rs.getString("createdAt"));
        return crawlResult;
    }
}