import { Field, Int, ObjectType } from "@nestjs/graphql";

@ObjectType()
export class ShowAnswerDTO {

    @Field(type => Int)
    id: number;

    @Field()
    answerString: string;
}