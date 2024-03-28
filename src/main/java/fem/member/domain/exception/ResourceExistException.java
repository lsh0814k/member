package fem.member.domain.exception;

public class ResourceExistException extends RuntimeException {
    public ResourceExistException(String datasource, String id) {
        super(datasource + "에서 " + id + "가 이미 존재 합니다.");
    }
}
