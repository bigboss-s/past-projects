import { Field, Int, ObjectType } from "@nestjs/graphql";

@ObjectType()
export class ResultDTO {
    @Field(type => Int)
    scoredPoints: number;

    @Field(type => Int)
    maxPoints: number;
}